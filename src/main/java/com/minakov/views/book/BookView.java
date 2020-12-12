package com.minakov.views.book;

import com.minakov.entity.Author;
import com.minakov.entity.Book;
import com.minakov.entity.Genre;
import com.minakov.entity.Publisher;
import com.minakov.services.AuthorService;
import com.minakov.services.BookService;
import com.minakov.services.GenreService;
import com.minakov.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Optional;

@Route(value = "books", layout = MainView.class)
@PageTitle("Book")
@CssImport("./styles/views/view/view-style.css")
public class BookView extends Div {

    private Grid<Book> grid = new Grid<>(Book.class);

    private TextField name;
    private ComboBox<String> author;
    private ComboBox<String> genre;
    private ComboBox<String> publisher;
    private IntegerField year;
    private TextField city;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private Binder<Book> binder;

    private BookService service = new BookService();
    private AuthorService authorService = new AuthorService();
    private GenreService genreService = new GenreService();

    private Book book;

    public BookView() {
        setId("book-view");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.setColumns("name", "author", "genre", "publisher", "year", "city");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.setItems(service.getAll());

        addFiltersToGrid();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Book> optionalBook = Optional.of(service.get(event.getValue().getId()));
                populateForm(optionalBook.get());
            } else {
                clearForm();
            }
        });

        binder = new BeanValidationBinder<>(Book.class);

        binder.bindInstanceFields(this);

        binder.forField(name).asRequired().bind(Book::getName, Book::setName);
        binder.forField(author).asRequired().bind(Book::getAuthor, Book::setAuthor);
        binder.forField(genre).asRequired().bind(Book::getGenre, Book::setGenre);
        binder.forField(publisher).asRequired().bind(Book::getPublisher, Book::setPublisher);
        binder.forField(year).asRequired().bind(Book::getYear, Book::setYear);
        binder.forField(city).asRequired().bind(Book::getCity, Book::setCity);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.book == null) {
                    this.book = new Book();
                }
                binder.writeBean(this.book);
                service.save(this.book);
                clearForm();
                refreshGrid();
                Notification.show("Data saved.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the data.");
            }
        });

        delete.addClickListener(e -> {
            if (this.book == null) {
                this.book = new Book();
            }
            service.delete(this.book.getId());
            clearForm();
            refreshGrid();
            Notification.show("Data deleted.");
        });
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(e -> grid.setItems(service.getAll(nameFilter.getValue(), 1)));
        filterRow.getCell(grid.getColumnByKey("name")).setComponent(nameFilter);

        TextField authorFilter = new TextField();
        authorFilter.setPlaceholder("Filter");
        authorFilter.setClearButtonVisible(true);
        authorFilter.setWidth("100%");
        authorFilter.setValueChangeMode(ValueChangeMode.EAGER);
        authorFilter.addValueChangeListener(
                e -> grid.setItems(service.getAll(authorFilter.getValue(), 2)));
        filterRow.getCell(grid.getColumnByKey("author")).setComponent(authorFilter);

        TextField publisherFilter = new TextField();
        publisherFilter.setPlaceholder("Filter");
        publisherFilter.setClearButtonVisible(true);
        publisherFilter.setWidth("100%");
        publisherFilter.setValueChangeMode(ValueChangeMode.EAGER);
        publisherFilter.addValueChangeListener(
                e -> grid.setItems(service.getAll(publisherFilter.getValue(), 3)));
        filterRow.getCell(grid.getColumnByKey("publisher")).setComponent(publisherFilter);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        author = new ComboBox<>("Author");
        author.setItems(setAuthors());
        genre = new ComboBox<>("Genre");
        genre.setItems(setGenres());
        publisher = new ComboBox<>("Publisher");
        publisher.setItems(setPublishers());
        year = new IntegerField("Publication year");
        city = new TextField("City");
        Component[] fields = new Component[]{name, author, genre, publisher, year, city};

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

    private void populateForm(Book value) {
        this.book = value;
        binder.readBean(this.book);
    }

    private ArrayList<String> setPublishers() {
        ArrayList<String> publishers = new ArrayList<>();
        for (Publisher temp : Publisher.values()) {
            publishers.add(temp.getPublisher());
        }
        return publishers;
    }

    private ArrayList<String> setGenres() {
        ArrayList<String> genres = new ArrayList<>();
        for (Genre temp : genreService.getAll()) {
            genres.add(temp.getName());
        }
        return genres;
    }

    private ArrayList<String > setAuthors() {
        ArrayList<String> authors = new ArrayList<>();
        for (Author temp : authorService.getAll()) {
            authors.add(temp.getName() + " " + temp.getSurname());
        }
        return authors;
    }
}
