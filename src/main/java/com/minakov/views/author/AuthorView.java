package com.minakov.views.author;

import com.minakov.entity.Author;
import com.minakov.entity.Genre;
import com.minakov.services.AuthorService;
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
import com.vaadin.flow.router.RouteAlias;

import java.util.Optional;

@Route(value = "authors", layout = MainView.class)
@PageTitle("Authors")
@CssImport("./styles/views/view/view-style.css")
@RouteAlias(value = "", layout = MainView.class)
public class AuthorView extends Div {

    private Grid<Author> grid = new Grid<>(Author.class);

    private final AuthorService service = new AuthorService();

    private TextField name;
    private TextField surname;
    private TextField secondName;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private Binder<Author> binder;

    private Author author;

    public AuthorView() {
        setId("author-view");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.setColumns("name", "surname", "secondName");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.setItems(service.getAll());

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Author> optionalAuthor = Optional.of(service.get(event.getValue().getId()));
                populateForm(optionalAuthor.get());
            } else {
                clearForm();
            }
        });

        binder = new BeanValidationBinder<>(Author.class);
        binder.bindInstanceFields(this);
        binder.forField(name).asRequired().bind(Author::getName, Author::setName);
        binder.forField(surname).asRequired().bind(Author::getSurname, Author::setSurname);
        binder.forField(secondName).asRequired().bind(Author::getSecondName, Author::setSecondName);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.author == null) {
                    this.author = new Author();
                }
                binder.writeBean(this.author);

                service.save(this.author);
                clearForm();
                refreshGrid();
                Notification.show("Data saved.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the data.");
            }
        });

        delete.addClickListener(e -> {
            if (this.author == null) {
                this.author = new Author();
            }
            service.delete(this.author.getId());
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
        surname = new TextField("Surname");
        secondName = new TextField("Second Name");
        Component[] fields = new Component[]{name, surname, secondName};

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

    private void populateForm(Author value) {
        this.author = value;
        binder.readBean(this.author);

    }
}
