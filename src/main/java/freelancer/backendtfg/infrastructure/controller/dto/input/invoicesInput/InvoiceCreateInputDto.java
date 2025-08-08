package freelancer.backendtfg.infrastructure.controller.dto.input.invoicesInput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "DTO de entrada para crear una factura")
public class InvoiceCreateInputDto {

    @ApiModelProperty(value = "ID del proyecto", required = true, example = "123")
    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long projectId;

    @ApiModelProperty(value = "Fecha de emisión", required = true, example = "2025-08-01")
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate issueDate;

    @ApiModelProperty(value = "Fecha de vencimiento", required = true, example = "2025-08-31")
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate dueDate;

    @ApiModelProperty(value = "Tarifa por hora", required = true, example = "25.50")
    @NotNull(message = "La tarifa por hora es obligatoria")
    @DecimalMin(value = "0.01", message = "La tarifa por hora debe ser mayor a 0")
    private BigDecimal hourlyRate;

    @ApiModelProperty(value = "Total de horas trabajadas", required = true, example = "40.0")
    @NotNull(message = "El total de horas es obligatorio")
    @DecimalMin(value = "0.0", message = "El total de horas no puede ser negativo")
    private BigDecimal totalHours;

    @ApiModelProperty(value = "Presupuesto del proyecto", required = true, example = "1500.00")
    @NotNull(message = "El presupuesto del proyecto es obligatorio")
    @DecimalMin(value = "0.0", message = "El presupuesto del proyecto no puede ser negativo")
    private BigDecimal projectBudget;

    @ApiModelProperty(value = "Costos adicionales", example = "100.00")
    @DecimalMin(value = "0.0", message = "Los costos adicionales no pueden ser negativos")
    private BigDecimal additionalCosts;

    @ApiModelProperty(value = "Porcentaje de impuestos", required = true, example = "21.0")
    @NotNull(message = "El porcentaje de impuestos es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje de impuestos no puede ser negativo")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "Moneda", required = true, example = "EUR")
    @NotNull(message = "La moneda es obligatoria")
    private String currency = "EUR";

    @ApiModelProperty(value = "Nombre del cliente", required = true, example = "Juan Pérez")
    @NotNull(message = "El nombre del cliente es obligatorio")
    private String clientName;

    @ApiModelProperty(value = "Email del cliente", required = true, example = "juan.perez@email.com")
    @NotNull(message = "El email del cliente es obligatorio")
    @Email(message = "El email del cliente debe tener un formato válido")
    private String clientEmail;

    @ApiModelProperty(value = "Teléfono del cliente", example = "+34123456789")
    private String clientPhone;

    @ApiModelProperty(value = "Dirección del cliente", example = "Calle Falsa 123, Madrid")
    private String clientAddress;

    @ApiModelProperty(value = "Número de identificación fiscal del cliente", example = "B12345678")
    private String clientTaxId;

    @ApiModelProperty(value = "Descripción de la factura", example = "Servicios de consultoría de software")
    private String description;

    @ApiModelProperty(value = "Notas adicionales", example = "Pago en 30 días")
    private String notes;

    @ApiModelProperty(value = "Condiciones de pago", example = "Transferencia bancaria")
    private String paymentTerms;

    @ApiModelProperty(value = "Método de pago", example = "Tarjeta de crédito")
    private String paymentMethod;
}