package org.aplicacao;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.dicom.op.CFind;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.DicomParam;
import org.weasis.dicom.param.DicomState;

import javax.swing.*;
import java.util.List;

public class Controller {
    public ComboBox choiceBox;
    public TextField textField;
    public Button btSearch;
    public TableView<Attributes> table;
    public Label labelResults;
    public Label labelLog;

    private String address;
    private int porta;

    public void initialize() {
        address = JOptionPane.showInputDialog("Insira o endereço do servidor", "dicomserver.co.uk");
        porta = Integer.parseInt(JOptionPane.showInputDialog("Insira a porta", "11112"));

        DicomParam[] params = {
                new DicomParam(Tag.AccessionNumber),
                new DicomParam(Tag.PatientID),
                new DicomParam(Tag.PatientName),
                new DicomParam(Tag.PatientSex),
                new DicomParam(Tag.PatientBirthDate),
                new DicomParam(Tag.StudyDescription),
                new DicomParam(Tag.StudyDate),
                new DicomParam(Tag.StudyInstanceUID),
                new DicomParam(Tag.StudyID),
        };

        ObservableList<String> data = FXCollections.observableArrayList();
        data.add(new DicomParam(Tag.AccessionNumber).getTagName());
        data.add(new DicomParam(Tag.PatientID).getTagName());
        data.add(new DicomParam(Tag.PatientName).getTagName());
        choiceBox.setItems(data);

        for (DicomParam param : params) {
            TableColumn<Attributes, String> coluna = new TableColumn<>(param.getTagName());
            coluna.setCellValueFactory(p ->
                    new SimpleStringProperty(p.getValue().getString(param.getTag(), "")));
            table.getColumns().add(coluna);
        }
    }

    public void buscaEntradas(ActionEvent actionEvent) {
        String accessNumber = null;
        String patientId = null;
        String patientName = null;
        if (choiceBox.getValue() != null) {
            switch (choiceBox.getValue().toString()) {
                case "AccessionNumber":
                    accessNumber = textField.getText();
                    break;
                case "PatientID":
                    patientId = textField.getText();
                    break;
                case "PatientName":
                    patientName = textField.getText();
                    break;
            }
        }

        DicomParam[] params = {
                new DicomParam(Tag.AccessionNumber, accessNumber),
                new DicomParam(Tag.PatientID, patientId),
                new DicomParam(Tag.PatientName, patientName),
                new DicomParam(Tag.PatientSex),
                new DicomParam(Tag.PatientBirthDate),
                new DicomParam(Tag.StudyDescription),
                new DicomParam(Tag.StudyDate),
                new DicomParam(Tag.StudyInstanceUID),
                new DicomParam(Tag.StudyID),
        };

        DicomNode calling = new DicomNode("UFCSPA");
        DicomNode called = new DicomNode("DICOMSERVER", address, porta);
        DicomState state = CFind.process(calling, called, params);

        List<Attributes> items = state.getDicomRSP();
        ObservableList<Attributes> data = FXCollections.observableArrayList(items);
        table.setItems(data);
        labelResults.setText(items.size() + " resultados");
        labelLog.setText(state.getMessage());
    }
}
