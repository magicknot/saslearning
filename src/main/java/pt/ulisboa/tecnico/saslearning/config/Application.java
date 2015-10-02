package pt.ulisboa.tecnico.saslearning.config;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.saslearning.domain.User;
import pt.ulisboa.tecnico.saslearning.utils.Utils;

@SpringBootApplication
@ComponentScan(basePackages = { "pt.ulisboa.tecnico.saslearning.*" })
public class Application extends SpringBootServletInitializer implements
		InitializingBean {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	@Atomic(mode = TxMode.WRITE)
	public void afterPropertiesSet() throws Exception {
		addNewUser("teacher", "1234567", "Catarina", "Santana", "TEACHER");
		// FileInputStream fis = new
		// FileInputStream("src/main/resources/ASof517_-_Alunos.xls");

		Resource resource = new ClassPathResource("ASof517_-_Alunos.xls");
		InputStream fis = resource.getInputStream();
		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		for (int r = 0; r < rows; r++) {
			HSSFRow row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			HSSFCell usernameCell = row.getCell(0); // Provide Correct cell
													// number
			String username = usernameCell.getStringCellValue();
			HSSFCell nameCell = row.getCell(2); // Provide Correct cell number
			String name = nameCell.getStringCellValue();
			String[] names = name.split(" ");
			String firstName = names[0];
			int ln = names.length - 1;
			String lastName = names[ln];
			HSSFCell passwordCell = row.getCell(13); // Provide Correct cell
														// number
			String password = "";
			if (passwordCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				int pass = (int) passwordCell.getNumericCellValue();
				password += pass;
			} else {
				password = passwordCell.getStringCellValue();
			}
			HSSFCell typeCell = row.getCell(14); // Provide Correct cell number
			String type = typeCell.getStringCellValue();
			addNewUser(username, password, firstName, lastName, type);
		}
		wb.close();

	}

	@Atomic(mode = TxMode.WRITE)
	private void addNewUser(String username, String password, String firstName,
			String lastName, String type) {
		if (!Utils.userExists(username)) {
			User u = new User();
			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setUsername(username);
			u.setPassword(password);
			u.setType(type);
			FenixFramework.getDomainRoot().addUser(u);
		}
	}
}
