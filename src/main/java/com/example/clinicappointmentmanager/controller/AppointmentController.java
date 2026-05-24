package com.example.clinicappointmentmanager.controller;

import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.AppointmentRepository;
import com.example.clinicappointmentmanager.view.AppointmentView;

public class AppointmentController {

    private final AppointmentRepository repository;
    private final AppointmentView view;

    public AppointmentController(
            AppointmentRepository repository,
            AppointmentView view) {

        this.repository = repository;
        this.view = view;
    }

    public synchronized void addAppointment(Appointment appointment) {

        Appointment existing =
                repository.findById(appointment.getAppointmentId());

        if (existing != null) {

            view.showErrorMessage(
                    "Appointment already exists with ID "
                            + appointment.getAppointmentId(),
                    existing
            );

            return;
        }

        repository.save(appointment);
        view.addAppointment(appointment);
    }

    public void getAllAppointments() {

        view.displayAppointments(repository.findAll());
    }

    public synchronized void updateAppointment(Appointment appointment) {

        Appointment existing =
                repository.findById(appointment.getAppointmentId());

        if (existing == null) {

            view.showErrorMessage(
                    "No appointment found with ID "
                            + appointment.getAppointmentId(),
                    appointment
            );

            return;
        }

        repository.update(appointment);
        view.updateAppointment(appointment);
    }

    public synchronized void deleteAppointment(Appointment appointment) {

        Appointment existing =
                repository.findById(appointment.getAppointmentId());

        if (existing == null) {

            view.showErrorMessage(
                    "No appointment found with ID "
                            + appointment.getAppointmentId(),
                    appointment
            );

            return;
        }

        repository.delete(appointment.getAppointmentId());
        view.deleteAppointment(appointment);
    }
}