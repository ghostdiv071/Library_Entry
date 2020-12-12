package com.minakov.views.genre;

import com.minakov.entity.Genre;
import com.minakov.services.GenreService;
import com.minakov.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@Route(value = "genres", layout = MainView.class)
@PageTitle("Genres")
@CssImport("./styles/views/view/view-style.css")
public class GenreView extends Div {

    private Grid<Genre> grid = new Grid<>(Genre.class);

    private TextField name;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private Binder<Genre> binder;

    private final GenreService service = new GenreService();

    private Genre genre;

    public GenreView() {
        setId("genre-view");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.setColumns("name");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.setItems(service.getAll());

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Genre> optionalGenre = Optional.of(service.get(event.getValue().getId()));
                populateForm(optionalGenre.get());
            } else {
                clearForm();
            }
        });

        binder = new BeanValidationBinder<>(Genre.class);

        binder.bindInstanceFields(this);

        binder.forField(name).asRequired().bind(Genre::getName, Genre::setName);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.genre == null) {
                    this.genre = new Genre();
                }
                binder.writeBean(this.genre);
                service.save(this.genre);
                clearForm();
                refreshGrid();
                Notification.show("Data saved.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the data.");
            }
        });

        delete.addClickListener(e -> {
            if (this.genre == null) {
                this.genre = new Genre();
            }
            service.delete(this.genre.getId());
            clearForm();
            refreshGrid();
            Notification.show("Data deleted.");
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        Component[] fields = new Component[]{name};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(service.getAll());
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Genre value) {
        this.genre = value;
        binder.readBean(this.genre);

    }
}
