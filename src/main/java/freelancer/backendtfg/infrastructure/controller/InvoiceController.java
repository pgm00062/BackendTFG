package freelancer.backendtfg.infrastructure.controller;

import freelancer.backendtfg.application.port.CreateInvoiceUseCase;
import freelancer.backendtfg.application.port.UpdateInvoiceUseCase;
import freelancer.backendtfg.application.port.ListUserInvoicesUseCase;
import freelancer.backendtfg.application.port.GetInvoiceByIdUseCase;
import freelancer.backendtfg.infrastructure.controller.dto.input.InvoiceCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.InvoiceOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import freelancer.backendtfg.application.port.DeleteInvoiceUseCase;
import javax.validation.Valid;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final UpdateInvoiceUseCase updateInvoiceUseCase;
    private final ListUserInvoicesUseCase listUserInvoicesUseCase;
    private final GetInvoiceByIdUseCase getInvoiceByIdUseCase;
    private final DeleteInvoiceUseCase deleteInvoiceUseCase;
    @PostMapping("/create")
    public ResponseEntity<InvoiceOutputDto> createInvoice(@AuthenticationPrincipal String email,
                                                         @Valid @RequestBody InvoiceCreateInputDto inputDto) {
        InvoiceOutputDto invoice = createInvoiceUseCase.createInvoice(email, inputDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InvoiceOutputDto> updateInvoice(@PathVariable Long id,
                                                         @AuthenticationPrincipal String email,
                                                         @Valid @RequestBody InvoiceCreateInputDto inputDto) {
        InvoiceOutputDto invoice = updateInvoiceUseCase.updateInvoice(id, email, inputDto);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<InvoiceOutputDto>> listInvoices(@AuthenticationPrincipal String email,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Page<InvoiceOutputDto> invoices = listUserInvoicesUseCase.listInvoices(email, PageRequest.of(page, size));
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceOutputDto> getInvoiceById(@PathVariable Long id,
                                                          @AuthenticationPrincipal String email) {
        InvoiceOutputDto invoice = getInvoiceByIdUseCase.getInvoiceById(id, email);
        return ResponseEntity.ok(invoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id,
                                              @AuthenticationPrincipal String email) {
        deleteInvoiceUseCase.deleteInvoice(id, email);
        return ResponseEntity.noContent().build();
    }
} 