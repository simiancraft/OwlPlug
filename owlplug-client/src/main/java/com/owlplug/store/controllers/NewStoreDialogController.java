/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.owlplug.store.model.Store;
import com.owlplug.store.services.StoreService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewStoreDialogController extends AbstractDialogController implements IEntityCreateOrUpdate<Store> {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private StoreService pluginStoreService;
  @Autowired
  private StoreMenuController storeMenuController;

  @FXML
  private JFXTextField storeUrlTextField;
  @FXML
  private JFXSpinner progressSpinner;
  @FXML
  private Label errorLabel;
  @FXML
  private JFXButton okButton;
  @FXML
  private JFXButton cancelButton;

  public NewStoreDialogController() {
    super(500, 200);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {

    progressSpinner.setVisible(false);
    errorLabel.setVisible(false);

    okButton.setOnAction(e -> {
      getPluginStore();
    });

    cancelButton.setOnAction(e -> {
      close();
    });

  }

  @Override
  public void startCreateSequence() {
    storeUrlTextField.setText("");
    progressSpinner.setVisible(false);
    errorLabel.setVisible(false);

  }

  @Override
  public void startUpdateSequence(Store entity) {
    throw new UnsupportedOperationException();

  }

  private void getPluginStore() {

    progressSpinner.setVisible(true);
    String storeUrl = storeUrlTextField.getText();

    if (storeUrl != null && !storeUrl.isEmpty()) {
      Task<Store> task = new Task<Store>() {
        @Override
        protected Store call() throws Exception {
          return pluginStoreService.getPluginStoreFromUrl(storeUrl);
        }
      };

      task.setOnSucceeded(e -> {
        Store pluginStore = task.getValue();
        progressSpinner.setVisible(false);
        if (pluginStore != null) {
          errorLabel.setVisible(false);
          pluginStoreService.save(pluginStore);
          storeMenuController.refreshView();
          close();
          this.getDialogController().newSimpleInfoDialog("Success",
              "The plugin store " + pluginStore.getName() + " has been sucessfully added !").show();
          this.getAnalyticsService().pageView("/app/store/action/add");
        } else {
          errorLabel.setVisible(true);
        }
      });
      new Thread(task).start();

    }

  }

  @Override
  protected Node getBody() {
    return lazyViewRegistry.getAsNode(LazyViewRegistry.NEW_STORE_VIEW);
  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Add a new store");
    title.getStyleClass().add("heading-3");

    ImageView iv = new ImageView(this.getApplicationDefaults().storeImage);
    iv.setFitHeight(20);
    iv.setFitWidth(20);
    title.setGraphic(iv);
    return title;
  }

}
