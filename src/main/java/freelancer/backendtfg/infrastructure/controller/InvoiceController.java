package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.invoiceUseCasePort.CreateInvoiceUseCase;
import freelancer.backendtfg.application.port.invoiceUseCasePort.UpdateInvoiceUseCase;
import freelancer.backendtfg.application.port.invoiceUseCasePort.ListUserInvoicesUseCase;
import freelancer.backendtfg.application.port.invoiceUseCasePort.GetInvoiceByIdUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.invoicesInput.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput.InvoiceOutputDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final UpdateInvoiceUseCase updateInvoiceUseCase;
    private final ListUserInvoicesUseCase listUserInvoicesUseCase;
    private final GetInvoiceByIdUseCase getInvoiceByIdUseCase;

    @ApiOperation(value = "Crear factura", notes = "Crea una nueva factura para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Factura creada exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PostMapping("/create")
    public ResponseEntity<InvoiceOutputDto> createInvoice(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody InvoiceCreateInputDto inputDto) {
        InvoiceOutputDto invoice = createInvoiceUseCase.createInvoice(email, inputDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @ApiOperation(value = "Actualizar factura", notes = "Actualiza una factura existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Factura actualizada exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada no válidos"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Factura no encontrada"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<InvoiceOutputDto> updateInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal String email,
            @Valid @RequestBody InvoiceCreateInputDto inputDto) {
        InvoiceOutputDto invoice = updateInvoiceUseCase.updateInvoice(id, email, inputDto);
        return ResponseEntity.ok(invoice);
    }

    @ApiOperation(value = "Listar facturas", notes = "Obtiene una lista paginada de facturas del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listado de facturas obtenido exitosamente"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/list")
    public ResponseEntity<Page<InvoiceOutputDto>> listInvoices(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InvoiceOutputDto> invoices = listUserInvoicesUseCase.listInvoices(email, PageRequest.of(page, size));
        return ResponseEntity.ok(invoices);
    }

    @ApiOperation(value = "Obtener factura por ID", notes = "Obtiene una factura por su ID para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Factura encontrada"),
            @ApiResponse(code = 401, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Factura no encontrada"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @GetMapping("/getInvoice/{id}")
    public ResponseEntity<InvoiceOutputDto> getInvoiceById(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        InvoiceOutputDto invoice = getInvoiceByIdUseCase.getInvoiceById(id, email);
        return ResponseEntity.ok(invoice);
    }

}