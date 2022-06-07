package com.voc.raceEntry.Service;


import com.voc.raceEntry.Entity.Event;
import com.voc.raceEntry.Entity.EventView;

import java.util.List;


public interface EventService {
    public List<EventView> getOpenEventsView();
    public List<Event> getOpenEvents();
    public Event getEvent(long id);
    public void  updateEvent(Event event);
    public List<Event> getAllEvents();
    /*public Customer getCustomer(int id);
    public void deleteCustomer(int id);
    public void addCustomer(Customer customer);
    public List<Employee> getEmployees();
    public List<Customer> searchCustomers(String name);

     */
}
