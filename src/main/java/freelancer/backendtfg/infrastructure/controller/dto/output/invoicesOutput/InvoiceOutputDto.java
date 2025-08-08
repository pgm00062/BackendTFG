package freelancer.backendtfg.infrastructure.controller.dto.output.invoicesOutput;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import freelancer.backendtfg.domain.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO de salida que representa una factura")
public class InvoiceOutputDto {

    @ApiModelProperty(value = "ID único de la factura", example = "123")
    private Long id;

    @ApiModelProperty(value = "Número de factura", example = "INV-2025-001")
    private String invoiceNumber;

    @ApiModelProperty(value = "Fecha de emisión de la factura", example = "2025-08-01")
    private LocalDate issueDate;

    @ApiModelProperty(value = "Fecha de vencimiento de la factura", example = "2025-08-31")
    private LocalDate dueDate;

    @ApiModelProperty(value = "Estado de la factura", example = "PENDIENTE")
    private InvoiceStatus status;

    // Información económica
    @ApiModelProperty(value = "Subtotal de la factura", example = "1000.00")
    private BigDecimal subtotal;

    @ApiModelProperty(value = "Porcentaje de impuestos aplicado", example = "21.0")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "Cantidad de impuestos calculada", example = "210.00")
    private BigDecimal taxAmount;

    @ApiModelProperty(value = "Monto total de la factura", example = "1210.00")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "Moneda utilizada", example = "EUR")
    private String currency;

    // Cálculos de tiempo
    @ApiModelProperty(value = "Total de horas facturadas", example = "40.0")
    private BigDecimal totalHours;

    @ApiModelProperty(value = "Tarifa por hora aplicada", example = "25.00")
    private BigDecimal hourlyRate;

    @ApiModelProperty(value = "Costo total por tiempo", example = "1000.00")
    private BigDecimal timeCost;

    // Desglose del proyecto
    @ApiModelProperty(value = "Presupuesto del proyecto", example = "1500.00")
    private BigDecimal projectBudget;

    @ApiModelProperty(value = "Costos adicionales relacionados", example = "100.00")
    private BigDecimal additionalCosts;

    @ApiModelProperty(value = "Descripción del proyecto o factura", example = "Desarrollo de funcionalidad de pagos")
    private String description;

    // Información del cliente
    @ApiModelProperty(value = "Nombre del cliente", example = "Juan Pérez")
    private String clientName;

    @ApiModelProperty(value = "Email del cliente", example = "juan.perez@example.com")
    private String clientEmail;

    @ApiModelProperty(value = "Teléfono del cliente", example = "+34123456789")
    private String clientPhone;

    @ApiModelProperty(value = "Dirección del cliente", example = "Calle Falsa 123, Madrid")
    private String clientAddress;

    @ApiModelProperty(value = "Identificación fiscal del cliente", example = "A12345678")
    private String clientTaxId;

    // Información adicional
    @ApiModelProperty(value = "Notas adicionales", example = "Pago a realizar en 30 días")
    private String notes;

    @ApiModelProperty(value = "Términos de pago", example = "Transferencia bancaria")
    private String paymentTerms;

    @ApiModelProperty(value = "Método de pago", example = "Tarjeta de crédito")
    private String paymentMethod;

    // Información del proyecto y usuario
    @ApiModelProperty(value = "ID del proyecto asociado", example = "10")
    private Long projectId;

    @ApiModelProperty(value = "Nombre del proyecto asociado", example = "Proyecto Alpha")
    private String projectName;

    @ApiModelProperty(value = "ID del usuario responsable", example = "5")
    private Long userId;

    @ApiModelProperty(value = "Nombre del usuario responsable", example = "Pablo García")
    private String userName;

    // Métodos de formato
    public String getFormattedSubtotal() {
        return String.format("%.2f %s", subtotal, currency);
    }
    
    public String getFormattedTotalAmount() {
        return String.format("%.2f %s", totalAmount, currency);
    }
    
    public String getFormattedTimeCost() {
        return String.format("%.2f %s", timeCost, currency);
    }
    
    public String getFormattedTaxAmount() {
        return String.format("%.2f %s", taxAmount, currency);
    }
} 