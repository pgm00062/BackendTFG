package freelancer.backendtfg.infrastructure.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import freelancer.backendtfg.domain.enums.InvoiceStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    // Información económica
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    private String currency = "EUR";
    
    // Cálculos de tiempo
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal totalHours;
    
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal hourlyRate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal timeCost;
    
    // Desglose del proyecto
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal projectBudget;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal additionalCosts;
    
    @Column(length = 1000)
    private String description;
    
    // Información del cliente
    @Column(nullable = false, length = 100)
    private String clientName;
    
    @Column(nullable = false, length = 100)
    private String clientEmail;
    
    @Column(length = 100)
    private String clientPhone;
    
    @Column(length = 200)
    private String clientAddress;
    
    @Column(length = 50)
    private String clientTaxId; // NIF/CIF del cliente
    
    // Información adicional
    @Column(length = 500)
    private String notes;
    
    @Column(length = 200)
    private String paymentTerms;
    
    @Column(length = 100)
    private String paymentMethod;
    
    // Relaciones
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    // Métodos de cálculo
    public void calculateTotals() {
        // Calcular subtotal
        this.subtotal = this.projectBudget.add(this.timeCost);
        if (this.additionalCosts != null) {
            this.subtotal = this.subtotal.add(this.additionalCosts);
        }
        
        // Calcular impuestos
        this.taxAmount = this.subtotal.multiply(this.taxRate.divide(new BigDecimal("100")));
        
        // Calcular total
        this.totalAmount = this.subtotal.add(this.taxAmount);
    }
    
    public void calculateTimeCost() {
        this.timeCost = this.totalHours.multiply(this.hourlyRate);
    }
    
    // Método para generar número de factura automático
    public void generateInvoiceNumber() {
        if (this.invoiceNumber == null || this.invoiceNumber.isEmpty()) {
            String year = String.valueOf(LocalDate.now().getYear());
            String month = String.format("%02d", LocalDate.now().getMonthValue());
            this.invoiceNumber = "FAC-" + year + "-" + month + "-" + String.format("%03d", this.id != null ? this.id : 1);
        }
    }
} 