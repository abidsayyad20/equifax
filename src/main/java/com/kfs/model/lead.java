package com.kfs.model;



import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "leads")
public class lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_cibil")
    private Integer isCibil;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_category_id")
    private Integer productCategoryId;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "loan_amount")
    private Integer loanAmount;

    @Column(name = "annual_income")
    private Integer annualIncome;

    @Column(name = "monthly_income")
    private Integer monthlyIncome;

    @Column(name = "encrypted_panno")
    private String encryptedPanno;

    @Column(name = "panno_digest")
    private String pannoDigest;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "lead_source")
    private String leadSource;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "tenure")
    private Integer tenure;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;
}
