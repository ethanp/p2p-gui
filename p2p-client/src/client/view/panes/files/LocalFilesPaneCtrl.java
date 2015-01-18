package client.view.panes.files;

import client.Main;
import p2p.file.meta.MetaP2PFile;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.List;

/**
 * Ethan Petuchowski 1/9/15
 */
public class LocalFilesPaneCtrl {

    @FXML private TableView<MetaP2PFile> localFileTable;
    @FXML private TableColumn<MetaP2PFile,MetaP2PFile> nameCol;
    @FXML private TableColumn<MetaP2PFile,MetaP2PFile> sizeCol;
    @FXML private TableColumn<MetaP2PFile,MetaP2PFile> percentCol;

    Callback<TableColumn.CellDataFeatures<MetaP2PFile, MetaP2PFile>,ObservableValue<MetaP2PFile>>
                valueFactory = cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue());

    private List<TableColumn<MetaP2PFile,MetaP2PFile>> tableColumns;

    private final ListChangeListener<MetaP2PFile> localFilesListener = c -> {
        while (c.next()) {
            if (c.wasRemoved()) {
                for (MetaP2PFile removedFile : c.getRemoved()) {
                    MetaP2PFile toRemove = null;
                    for (MetaP2PFile localItem : localFileTable.getItems()) {
                        if (removedFile.equals(localItem)) {
                            toRemove = localItem;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        localFileTable.getItems().remove(toRemove);
                    }
                }
            }
            if (c.wasAdded()) {
                for (MetaP2PFile addedFile : c.getAddedSubList()) {
                    localFileTable.getItems().add(addedFile);
                }
            }
        }
    };

    @FXML private void initialize() {
        localFileTable.setEditable(false);
        localFileTable.getItems().addAll(Main.getLocalFiles());
        Main.getLocalFiles().addListener(localFilesListener);
        tableColumns = Arrays.asList(nameCol, sizeCol, percentCol);
        tableColumns.stream().forEach(c -> c.setCellValueFactory(valueFactory));

        nameCol.setCellFactory(e -> new LocalFileCell(LocalFileCell.Col.NAME));
        sizeCol.setCellFactory(e -> new LocalFileCell(LocalFileCell.Col.SIZE));
        percentCol.setCellFactory(e -> new LocalFileCell(LocalFileCell.Col.PERCENT));

        // this says "when a selection is made in the fileTable, do whatever I want"
        localFileTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fileWasSelected(newValue));

    }
    private void fileWasSelected(MetaP2PFile metaP2PFile) {}
}
