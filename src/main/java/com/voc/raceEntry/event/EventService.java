package com.voc.raceEntry.event;


import java.util.List;


public interface EventService {
    List<EventView> getOpenEventsView();

    List<Event> getOpenEvents();

    Event getEvent(long id);

    void updateEvent(Event event);

    void deleteEvent(long id);

    List<Event> getAllEvents();
    /*public Customer getCustomer(int id);
    public void deleteCustomer(int id);
    public void addCustomer(Customer customer);
    public List<Employee> getEmployees();
    public List<Customer> searchCustomers(String name);

     */
}
