package com.example.clinicappointmentmanager.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.example.clinicappointmentmanager.controller.AppointmentController;
import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.view.AppointmentView;

public class AppointmentSwingView extends JFrame implements AppointmentView {

    private static final long serialVersionUID = 1L;

    private static final int REASON_MAX_LENGTH = 200;

    private JTextField txtAppointmentId;
    private JTextField txtPatientName;
    private JTextField txtDoctorName;
    private JTextField txtAppointmentDate;
    private JTextField txtAppointmentTime;
    private JTextField txtReason;

    private JButton btnAddAppointment;
    private JButton btnUpdateAppointment;
    private JButton btnDeleteAppointment;
    private JButton btnClear;

    private JLabel lblErrorMessage;

    private JList<Appointment> listAppointments;
    private DefaultListModel<Appointment> listAppointmentModel;

    private transient AppointmentController appointmentController;

    public void setAppointmentController(AppointmentController appointmentController) {
        this.appointmentController = appointmentController;
    }

    public DefaultListModel<Appointment> getListAppointmentModel() {
        return listAppointmentModel;
    }

    public AppointmentSwingView() {
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Clinic Appointment Manager");

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 45, 0, 770, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 35, 173, 35, 35, 35, 23, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] {
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
        };
        getContentPane().setLayout(gridBagLayout);

        JLabel lblAppointmentId = new JLabel("Appointment ID");
        GridBagConstraints gbc_lblAppointmentId = new GridBagConstraints();
        gbc_lblAppointmentId.anchor = GridBagConstraints.EAST;
        gbc_lblAppointmentId.insets = new Insets(0, 0, 5, 5);
        gbc_lblAppointmentId.gridx = 1;
        gbc_lblAppointmentId.gridy = 0;
        getContentPane().add(lblAppointmentId, gbc_lblAppointmentId);

        txtAppointmentId = new JTextField();
        txtAppointmentId.setName("appointmentidTextBox");
        GridBagConstraints gbc_txtAppointmentId = new GridBagConstraints();
        gbc_txtAppointmentId.insets = new Insets(0, 0, 5, 5);
        gbc_txtAppointmentId.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtAppointmentId.gridx = 2;
        gbc_txtAppointmentId.gridy = 0;
        getContentPane().add(txtAppointmentId, gbc_txtAppointmentId);

        JLabel lblPatientName = new JLabel("Patient Name");
        GridBagConstraints gbc_lblPatientName = new GridBagConstraints();
        gbc_lblPatientName.anchor = GridBagConstraints.EAST;
        gbc_lblPatientName.insets = new Insets(0, 0, 5, 5);
        gbc_lblPatientName.gridx = 1;
        gbc_lblPatientName.gridy = 1;
        getContentPane().add(lblPatientName, gbc_lblPatientName);

        txtPatientName = new JTextField();
        txtPatientName.setName("patientnameTextBox");
        GridBagConstraints gbc_txtPatientName = new GridBagConstraints();
        gbc_txtPatientName.insets = new Insets(0, 0, 5, 5);
        gbc_txtPatientName.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPatientName.gridx = 2;
        gbc_txtPatientName.gridy = 1;
        getContentPane().add(txtPatientName, gbc_txtPatientName);

        JLabel lblDoctorName = new JLabel("Doctor Name");
        GridBagConstraints gbc_lblDoctorName = new GridBagConstraints();
        gbc_lblDoctorName.anchor = GridBagConstraints.EAST;
        gbc_lblDoctorName.insets = new Insets(0, 0, 5, 5);
        gbc_lblDoctorName.gridx = 1;
        gbc_lblDoctorName.gridy = 2;
        getContentPane().add(lblDoctorName, gbc_lblDoctorName);

        txtDoctorName = new JTextField();
        txtDoctorName.setName("doctornameTextBox");
        GridBagConstraints gbc_txtDoctorName = new GridBagConstraints();
        gbc_txtDoctorName.insets = new Insets(0, 0, 5, 5);
        gbc_txtDoctorName.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDoctorName.gridx = 2;
        gbc_txtDoctorName.gridy = 2;
        getContentPane().add(txtDoctorName, gbc_txtDoctorName);

        JLabel lblAppointmentDate = new JLabel("Appointment Date");
        GridBagConstraints gbc_lblAppointmentDate = new GridBagConstraints();
        gbc_lblAppointmentDate.anchor = GridBagConstraints.EAST;
        gbc_lblAppointmentDate.insets = new Insets(0, 0, 5, 5);
        gbc_lblAppointmentDate.gridx = 1;
        gbc_lblAppointmentDate.gridy = 3;
        getContentPane().add(lblAppointmentDate, gbc_lblAppointmentDate);

        txtAppointmentDate = new JTextField();
        txtAppointmentDate.setName("appointmentdateTextBox");
        GridBagConstraints gbc_txtAppointmentDate = new GridBagConstraints();
        gbc_txtAppointmentDate.insets = new Insets(0, 0, 5, 5);
        gbc_txtAppointmentDate.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtAppointmentDate.gridx = 2;
        gbc_txtAppointmentDate.gridy = 3;
        getContentPane().add(txtAppointmentDate, gbc_txtAppointmentDate);

        JLabel lblAppointmentTime = new JLabel("Appointment Time");
        GridBagConstraints gbc_lblAppointmentTime = new GridBagConstraints();
        gbc_lblAppointmentTime.anchor = GridBagConstraints.EAST;
        gbc_lblAppointmentTime.insets = new Insets(0, 0, 5, 5);
        gbc_lblAppointmentTime.gridx = 1;
        gbc_lblAppointmentTime.gridy = 4;
        getContentPane().add(lblAppointmentTime, gbc_lblAppointmentTime);

        txtAppointmentTime = new JTextField();
        txtAppointmentTime.setName("appointmenttimeTextBox");
        GridBagConstraints gbc_txtAppointmentTime = new GridBagConstraints();
        gbc_txtAppointmentTime.insets = new Insets(0, 0, 5, 5);
        gbc_txtAppointmentTime.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtAppointmentTime.gridx = 2;
        gbc_txtAppointmentTime.gridy = 4;
        getContentPane().add(txtAppointmentTime, gbc_txtAppointmentTime);

        JLabel lblReason = new JLabel("Reason");
        GridBagConstraints gbc_lblReason = new GridBagConstraints();
        gbc_lblReason.anchor = GridBagConstraints.EAST;
        gbc_lblReason.insets = new Insets(0, 0, 5, 5);
        gbc_lblReason.gridx = 1;
        gbc_lblReason.gridy = 5;
        getContentPane().add(lblReason, gbc_lblReason);

        txtReason = new JTextField();
        txtReason.setName("reasonTextBox");
        GridBagConstraints gbc_txtReason = new GridBagConstraints();
        gbc_txtReason.insets = new Insets(0, 0, 5, 5);
        gbc_txtReason.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtReason.gridx = 2;
        gbc_txtReason.gridy = 5;
        getContentPane().add(txtReason, gbc_txtReason);

        btnAddAppointment = new JButton("Add Appointment");
        btnAddAppointment.setEnabled(false);
        btnAddAppointment.setName("btnAddAppointment");
        btnAddAppointment.addActionListener(e -> new Thread(() -> {
            Appointment appointment = buildAppointmentFromFields();
            if (appointment != null) {
                appointmentController.addAppointment(appointment);
            }
        }).start());

        GridBagConstraints gbc_btnAddAppointment = new GridBagConstraints();
        gbc_btnAddAppointment.insets = new Insets(0, 0, 5, 5);
        gbc_btnAddAppointment.gridx = 2;
        gbc_btnAddAppointment.gridy = 6;
        getContentPane().add(btnAddAppointment, gbc_btnAddAppointment);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 7;
        getContentPane().add(scrollPane, gbc_scrollPane);

        listAppointmentModel = new DefaultListModel<>();
        listAppointments = new JList<>(listAppointmentModel);
        listAppointments.setName("appointmentList");
        listAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listAppointments.addListSelectionListener(e -> {
            boolean selected = listAppointments.getSelectedIndex() != -1;

            btnDeleteAppointment.setEnabled(selected);
            btnUpdateAppointment.setEnabled(selected);
            btnClear.setEnabled(selected);

            if (selected) {
                Appointment appointment = listAppointments.getSelectedValue();
                txtAppointmentId.setText(appointment.getAppointmentId());
                txtPatientName.setText(appointment.getPatientName());
                txtDoctorName.setText(appointment.getDoctorName());
                txtAppointmentDate.setText(appointment.getAppointmentDate());
                txtAppointmentTime.setText(appointment.getAppointmentTime());
                txtReason.setText(appointment.getReason());

                txtAppointmentId.setEnabled(false);
                btnAddAppointment.setEnabled(false);
            } else {
                clearFields();
                txtAppointmentId.setEnabled(true);
            }
        });

        listAppointments.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Appointment appointment = (Appointment) value;
                return super.getListCellRendererComponent(list, getDisplayString(appointment), index, isSelected, cellHasFocus);
            }
        });

        scrollPane.setViewportView(listAppointments);

        btnUpdateAppointment = new JButton("Update Appointment");
        btnUpdateAppointment.setEnabled(false);
        btnUpdateAppointment.setName("btnUpdateAppointment");
        btnUpdateAppointment.addActionListener(e -> new Thread(() -> {
            Appointment appointment = buildAppointmentFromFields();
            if (appointment != null) {
                appointmentController.updateAppointment(appointment);
            }
        }).start());

        GridBagConstraints gbc_btnUpdateAppointment = new GridBagConstraints();
        gbc_btnUpdateAppointment.insets = new Insets(0, 0, 5, 5);
        gbc_btnUpdateAppointment.gridx = 2;
        gbc_btnUpdateAppointment.gridy = 8;
        getContentPane().add(btnUpdateAppointment, gbc_btnUpdateAppointment);

        btnDeleteAppointment = new JButton("Delete Appointment");
        btnDeleteAppointment.setEnabled(false);
        btnDeleteAppointment.setName("btnDeleteAppointment");
        btnDeleteAppointment.addActionListener(e -> new Thread(() -> {
            Appointment selectedAppointment = listAppointments.getSelectedValue();
            if (selectedAppointment != null) {
                appointmentController.deleteAppointment(selectedAppointment);
            }
        }).start());

        GridBagConstraints gbc_btnDeleteAppointment = new GridBagConstraints();
        gbc_btnDeleteAppointment.insets = new Insets(0, 0, 5, 5);
        gbc_btnDeleteAppointment.gridx = 2;
        gbc_btnDeleteAppointment.gridy = 9;
        getContentPane().add(btnDeleteAppointment, gbc_btnDeleteAppointment);

        btnClear = new JButton("Clear");
        btnClear.setEnabled(false);
        btnClear.setName("btnClear");
        btnClear.addActionListener(e -> {
            clearFields();
            btnUpdateAppointment.setEnabled(false);
            btnDeleteAppointment.setEnabled(false);
            btnClear.setEnabled(false);
            listAppointments.clearSelection();
            txtAppointmentId.setEnabled(true);
        });

        GridBagConstraints gbc_btnClear = new GridBagConstraints();
        gbc_btnClear.insets = new Insets(0, 0, 5, 5);
        gbc_btnClear.gridx = 2;
        gbc_btnClear.gridy = 10;
        getContentPane().add(btnClear, gbc_btnClear);

        lblErrorMessage = new JLabel("");
        lblErrorMessage.setName("errorMessageLabel");
        GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
        gbc_lblErrorMessage.gridwidth = 3;
        gbc_lblErrorMessage.insets = new Insets(0, 0, 0, 5);
        gbc_lblErrorMessage.gridx = 0;
        gbc_lblErrorMessage.gridy = 11;
        getContentPane().add(lblErrorMessage, gbc_lblErrorMessage);

        KeyAdapter addEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean allFilled = !txtAppointmentId.getText().trim().isEmpty()
                        && !txtPatientName.getText().trim().isEmpty()
                        && !txtDoctorName.getText().trim().isEmpty()
                        && !txtAppointmentDate.getText().trim().isEmpty()
                        && !txtAppointmentTime.getText().trim().isEmpty()
                        && !txtReason.getText().trim().isEmpty();

                btnAddAppointment.setEnabled(listAppointments.getSelectedIndex() == -1 && allFilled);
            }
        };

        txtAppointmentId.addKeyListener(addEnabler);
        txtPatientName.addKeyListener(addEnabler);
        txtDoctorName.addKeyListener(addEnabler);
        txtAppointmentDate.addKeyListener(addEnabler);
        txtAppointmentTime.addKeyListener(addEnabler);
        txtReason.addKeyListener(addEnabler);
    }

    @Override
    public void displayAppointments(List<Appointment> appointments) {
        appointments.forEach(listAppointmentModel::addElement);
    }

    @Override
    public void addAppointment(Appointment appointment) {
        SwingUtilities.invokeLater(() -> {
            listAppointmentModel.addElement(appointment);
            resetErrorLabel();
            clearFields();
        });
    }

    @Override
    public void deleteAppointment(Appointment appointment) {
        SwingUtilities.invokeLater(() -> {
            listAppointmentModel.removeElement(appointment);
            resetErrorLabel();
            clearFields();
            txtAppointmentId.setEnabled(true);
        });
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < listAppointmentModel.size(); i++) {
                if (listAppointmentModel.get(i).getAppointmentId().equals(appointment.getAppointmentId())) {
                    listAppointmentModel.set(i, appointment);
                    break;
                }
            }
            resetErrorLabel();
            clearFields();
            txtAppointmentId.setEnabled(true);
            listAppointments.clearSelection();
        });
    }

    @Override
    public void showErrorMessage(String message, Appointment appointment) {
        SwingUtilities.invokeLater(() -> {
            String displayMessage = (appointment == null) ? "" : getDisplayString(appointment);
            lblErrorMessage.setText(message + displayMessage);
            if (appointment != null) {
                listAppointmentModel.removeElement(appointment);
            }
        });
    }

    private void resetErrorLabel() {
        lblErrorMessage.setText("");
    }

    private void clearFields() {
        txtAppointmentId.setText("");
        txtPatientName.setText("");
        txtDoctorName.setText("");
        txtAppointmentDate.setText("");
        txtAppointmentTime.setText("");
        txtReason.setText("");
    }

    private String getDisplayString(Appointment appointment) {
        return appointment.getAppointmentId() + " - " + appointment.getPatientName() + " - "
                + appointment.getDoctorName() + " - " + appointment.getAppointmentDate() + " - "
                + appointment.getAppointmentTime() + " - " + appointment.getReason();
    }

    private boolean isAppointmentIdValid(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("^CLN-\\d{3}$");
    }

    private boolean isLettersAndSpaces(String s) {
        if (s == null) {
            return false;
        }
        if (s.isEmpty()) {
            return false;
        }
        return s.matches("^[a-zA-Z ]+$");
    }

    private boolean isDateValid(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    private boolean isTimeValid(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("^\\d{2}:\\d{2}$");
    }

    private Appointment buildAppointmentFromFields() {
        String appointmentId = txtAppointmentId.getText().trim();
        String patientName = txtPatientName.getText().trim();
        String doctorName = txtDoctorName.getText().trim();
        String appointmentDate = txtAppointmentDate.getText().trim();
        String appointmentTime = txtAppointmentTime.getText().trim();
        String reason = txtReason.getText().trim();

        if (!isAppointmentIdValid(appointmentId)) {
            showErrorMessage("Appointment ID must be CLN- followed by 3 digits (e.g., CLN-001)", null);
            return null;
        }

        if (!isLettersAndSpaces(patientName)) {
            showErrorMessage("Patient Name must contain only letters and spaces", null);
            return null;
        }

        if (!isLettersAndSpaces(doctorName)) {
            showErrorMessage("Doctor Name must contain only letters and spaces", null);
            return null;
        }

        if (!isDateValid(appointmentDate)) {
            showErrorMessage("Appointment Date must be in format YYYY-MM-DD", null);
            return null;
        }

        if (!isTimeValid(appointmentTime)) {
            showErrorMessage("Appointment Time must be in format HH:MM", null);
            return null;
        }

        if (reason.length() > REASON_MAX_LENGTH) {
            showErrorMessage("Reason cannot exceed 200 characters", null);
            return null;
        }

        return new Appointment(appointmentId, patientName, doctorName, appointmentDate, appointmentTime, reason);
    }
}