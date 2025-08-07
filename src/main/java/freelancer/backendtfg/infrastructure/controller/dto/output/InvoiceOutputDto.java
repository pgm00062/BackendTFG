package freelancer.backendtfg.infrastructure.controller.dto.output;

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
public class InvoiceOutputDto {
    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    
    // Información económica
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    
    // Cálculos de tiempo
    private BigDecimal totalHours;
    private BigDecimal hourlyRate;
    private BigDecimal timeCost;
    
    // Desglose del proyecto
    private BigDecimal projectBudget;
    private BigDecimal additionalCosts;
    private String description;
    
    // Información del cliente
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String clientAddress;
    private String clientTaxId;
    
    // Información adicional
    private String notes;
    private String paymentTerms;
    private String paymentMethod;
    
    // Información del proyecto y usuario
    private Long projectId;
    private String projectName;
    private Long userId;
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