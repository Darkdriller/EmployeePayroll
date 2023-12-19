import com.bridgelabz.javafileio.entity.EmployeePayrollData;
import com.bridgelabz.javafileio.entity.EmployeeService;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Dhruv
 * @project EmployeePayroll
 */
public class EmployeePayrollDBTest {

        @Test
        public  void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
            EmployeeService employeePayrollService = new EmployeeService();
            List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeeService.IOService.DB_IO);
            Assert.assertEquals(5,employeePayrollData.size());
        }
}
