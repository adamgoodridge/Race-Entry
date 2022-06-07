package com.voc.raceEntry.Service;


import com.voc.raceEntry.Entity.Driver;

import java.util.List;


public interface DriverService {
    public Driver getDriver(long id);
    public void updateDriver(Driver driver);
    public List<Driver> getDrivers();

    void deleteDriver(long id);
    /*public Customer getCustomer(int id);
    public void deleteCustomer(int id);
    public void addCustomer(Customer customer);
    public List<Employee> getEmployees();
    public List<Customer> searchCustomers(String name);

     */
}
