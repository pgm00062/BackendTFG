package freelancer.backendtfg.application.useCaseImpl.invoiceUseCaseImpl;

import freelancer.backendtfg.application.port.invoiceUseCasePort.UpdateInvoiceUseCase;
import freelancer.backendtfg.domain.enums.InvoiceStatus;
import freelancer.backendtfg.domain.mapper.InvoiceMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.invoicesInput.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;
import freelancer.backendtfg.infrastructure.repository.port.InvoiceRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.ProjectRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.entity.InvoiceEntity;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdateInvoiceUseCaseImpl implements UpdateInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectRepositoryPort projectRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceOutputDto updateInvoice(Long invoiceId, String userEmail, InvoiceCreateInputDto inputDto) {
        // Validar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Validar que la factura existe y pertenece al usuario
        InvoiceEntity existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!existingInvoice.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("La factura no pertenece al usuario");
        }

        // Validar que el proyecto existe y pertenece al usuario
        ProjectEntity project = projectRepository.findById(inputDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("El proyecto no pertenece al usuario");
        }

        // Validar que la factura no esté en estado SENT o PAID
        if (existingInvoice.getStatus() == InvoiceStatus.SENT || existingInvoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("No se puede modificar una factura que ya ha sido enviada o pagada");
        }

        // Actualizar los campos de la factura
        updateInvoiceFields(existingInvoice, inputDto);
        
        // Establecer las relaciones
        existingInvoice.setProject(project);
        
        // Calcular los totales
        calculateInvoiceTotals(existingInvoice);
        
        // Guardar la factura actualizada
        InvoiceEntity savedInvoice = invoiceRepository.save(existingInvoice);
        
        // Retornar el DTO de salida
        return invoiceMapper.toOutputDto(savedInvoice);
    }

    private void updateInvoiceFields(InvoiceEntity invoice, InvoiceCreateInputDto inputDto) {
        invoice.setIssueDate(inputDto.getIssueDate());
        invoice.setDueDate(inputDto.getDueDate());
        invoice.setHourlyRate(inputDto.getHourlyRate());
        invoice.setTotalHours(inputDto.getTotalHours());
        invoice.setProjectBudget(inputDto.getProjectBudget());
        invoice.setAdditionalCosts(inputDto.getAdditionalCosts());
        invoice.setTaxRate(inputDto.getTaxRate());
        invoice.setCurrency(inputDto.getCurrency());
        invoice.setClientName(inputDto.getClientName());
        invoice.setClientEmail(inputDto.getClientEmail());
        invoice.setClientPhone(inputDto.getClientPhone());
        invoice.setClientAddress(inputDto.getClientAddress());
        invoice.setClientTaxId(inputDto.getClientTaxId());
        invoice.setDescription(inputDto.getDescription());
        invoice.setNotes(inputDto.getNotes());
        invoice.setPaymentTerms(inputDto.getPaymentTerms());
        invoice.setPaymentMethod(inputDto.getPaymentMethod());
    }

    private void calculateInvoiceTotals(InvoiceEntity invoice) {
        // Calcular el costo del tiempo
        BigDecimal timeCost = invoice.getTotalHours().multiply(invoice.getHourlyRate());
        invoice.setTimeCost(timeCost);
        
        // Calcular el subtotal
        BigDecimal subtotal = invoice.getProjectBudget().add(timeCost);
        if (invoice.getAdditionalCosts() != null) {
            subtotal = subtotal.add(invoice.getAdditionalCosts());
        }
        invoice.setSubtotal(subtotal);
        
        // Calcular el impuesto
        BigDecimal taxAmount = subtotal.multiply(invoice.getTaxRate().divide(new BigDecimal("100")));
        invoice.setTaxAmount(taxAmount);
        
        // Calcular el total
        BigDecimal totalAmount = subtotal.add(taxAmount);
        invoice.setTotalAmount(totalAmount);
    }
} 