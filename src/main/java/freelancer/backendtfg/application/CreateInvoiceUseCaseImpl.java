package freelancer.backendtfg.application;

import freelancer.backendtfg.application.port.CreateInvoiceUseCase;
import freelancer.backendtfg.domain.enums.InvoiceStatus;
import freelancer.backendtfg.domain.mapper.InvoiceMapper;
import freelancer.backendtfg.infrastructure.controller.dto.input.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.InvoiceOutputDto;
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
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateInvoiceUseCaseImpl implements CreateInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final UserRepositoryPort userRepository;
    private final ProjectRepositoryPort projectRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceOutputDto createInvoice(String userEmail, InvoiceCreateInputDto inputDto) {
        // Validar que el usuario existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Validar que el proyecto existe y pertenece al usuario
        ProjectEntity project = projectRepository.findById(inputDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("El proyecto no pertenece al usuario");
        }

        // Crear la entidad de factura
        InvoiceEntity invoice = invoiceMapper.toEntity(inputDto);
        
        // Establecer las relaciones
        invoice.setUser(user);
        invoice.setProject(project);
        
        // Establecer el estado por defecto si no se proporciona
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.DRAFT);
        }
        
        // Calcular los totales
        calculateInvoiceTotals(invoice);
        
        // Generar número de factura
        generateInvoiceNumber(invoice);
        
        // Guardar la factura
        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);
        
        // Retornar el DTO de salida
        return invoiceMapper.toOutputDto(savedInvoice);
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

    private void generateInvoiceNumber(InvoiceEntity invoice) {
        // Generar número de factura con formato: FAC-YYYY-MM-XXX
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());
        
        // Contar facturas del usuario para generar el número secuencial
        long invoiceCount = invoiceRepository.countByUserId(invoice.getUser().getId()) + 1;
        String sequential = String.format("%03d", invoiceCount);
        
        String invoiceNumber = "FAC-" + year + "-" + month + "-" + sequential;
        invoice.setInvoiceNumber(invoiceNumber);
    }
} 