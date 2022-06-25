package com.voc.raceEntry.Driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public void deleteDriver(long id) {
        Driver driver = driverRepository.getReferenceById(id);
        driverRepository.delete(driver);
    }

    public Driver getDriver(long id) {
        return  driverRepository.getReferenceById(id);
    }

    //add or update
    @Override
    public void updateDriver(Driver driver) {
        driverRepository.save(driver);
    }
/*
    @Transactional
    @Override
    public List<Customer> searchCustomers(String name) {
        return DriverDAO.searchCustomers(name);
    }
    @Override
    @Transactional
    public void deleteCustomer(int id) {
        DriverDAO.deleteCustomer(id);
    }

    @Transactional
    @Override
    public void addCustomer(Customer customer) {
        DriverDAO.saveCustomer(customer);
    }

    @Transactional
    @Override
    public List<Employee> getEmployees() {
        return employeeDAO.getEmployees();
    }
*/


}
