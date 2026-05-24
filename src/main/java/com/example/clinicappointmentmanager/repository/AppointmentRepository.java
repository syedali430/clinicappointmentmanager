package com.example.clinicappointmentmanager.repository;

import java.util.List;

import com.example.clinicappointmentmanager.model.Appointment;

public interface AppointmentRepository {

    void save(Appointment appointment);

    List<Appointment> findAll();

    Appointment findById(String appointmentId);

    void update(Appointment appointment);

    void delete(String appointmentId);
}