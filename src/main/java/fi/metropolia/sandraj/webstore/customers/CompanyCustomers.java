package fi.metropolia.sandraj.webstore.customers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "companycustomers")
public class CompanyCustomers extends Customers {

    @Column(name = "company_name", length = 255, nullable = false)
    private String companyName;

    @Column(name = "company_id", length = 20, nullable = false)
    private String companyId;

    public CompanyCustomers() {}

    public CompanyCustomers(
            String firstName, String lastName, String email, String phone,
            String companyName, String companyId)
    {
        super(firstName, lastName, email, phone);
        this.companyName = companyName;
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
