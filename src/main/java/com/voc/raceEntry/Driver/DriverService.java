package com.voc.raceEntry.Driver;


import java.util.List;


public interface DriverService {
    Driver getDriver(long id);

    void updateDriver(Driver driver);

    List<Driver> getDrivers();

    void deleteDriver(long id);
    /*public Customer getCustomer(int id);
    public void deleteCustomer(int id);
    public void addCustomer(Customer customer);
    public List<Employee> getEmployees();
    public List<Customer> searchCustomers(String name);

     */
}
