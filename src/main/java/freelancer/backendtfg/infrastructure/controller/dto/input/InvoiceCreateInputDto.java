package freelancer.backendtfg.infrastructure.controller.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreateInputDto {
    
    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long projectId;
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate issueDate;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate dueDate;
    
    @NotNull(message = "La tarifa por hora es obligatoria")
    @DecimalMin(value = "0.01", message = "La tarifa por hora debe ser mayor a 0")
    private BigDecimal hourlyRate;
    
    @NotNull(message = "El total de horas es obligatorio")
    @DecimalMin(value = "0.0", message = "El total de horas no puede ser negativo")
    private BigDecimal totalHours;
    
    @NotNull(message = "El presupuesto del proyecto es obligatorio")
    @DecimalMin(value = "0.0", message = "El presupuesto del proyecto no puede ser negativo")
    private BigDecimal projectBudget;
    
    @DecimalMin(value = "0.0", message = "Los costos adicionales no pueden ser negativos")
    private BigDecimal additionalCosts;
    
    @NotNull(message = "El porcentaje de impuestos es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje de impuestos no puede ser negativo")
    private BigDecimal taxRate;
    
    @NotNull(message = "La moneda es obligatoria")
    private String currency = "EUR";
    
    @NotNull(message = "El nombre del cliente es obligatorio")
    private String clientName;
    
    @NotNull(message = "El email del cliente es obligatorio")
    @Email(message = "El email del cliente debe tener un formato válido")
    private String clientEmail;
    
    private String clientPhone;
    
    private String clientAddress;
    
    private String clientTaxId;
    
    private String description;
    
    private String notes;
    
    private String paymentTerms;
    
    private String paymentMethod;
} 