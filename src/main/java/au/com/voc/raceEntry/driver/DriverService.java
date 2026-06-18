package au.com.voc.raceEntry.driver;

import org.springframework.stereotype.Service;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver create(String name, String licenseNumber) {
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("License number must not be blank");
        }
        if (driverRepository.findByLicenseNumber(licenseNumber).isPresent()) {
            throw new IllegalStateException("A driver with license number '" + licenseNumber + "' already exists");
        }
        Driver driver = new Driver();
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driverRepository.save(driver);
    }

    public Driver findById(Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));
    }
}
