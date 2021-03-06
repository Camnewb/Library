package org.whstsa.library.gui.components.tables;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.whstsa.library.LibraryDB;
import org.whstsa.library.api.BackgroundWorker;
import org.whstsa.library.api.IPerson;
import org.whstsa.library.api.ObservableReference;
import org.whstsa.library.api.books.IBook;
import org.whstsa.library.api.library.ICheckout;
import org.whstsa.library.api.library.ILibrary;
import org.whstsa.library.api.library.IMember;
import org.whstsa.library.db.Loader;
import org.whstsa.library.db.ObjectDelegate;
import org.whstsa.library.gui.api.GuiLibraryManager;
import org.whstsa.library.gui.api.GuiMain;
import org.whstsa.library.gui.api.GuiStatusBar;
import org.whstsa.library.gui.api.MainMenuBar;
import org.whstsa.library.gui.components.Table;
import org.whstsa.library.gui.dialogs.*;
import org.whstsa.library.gui.factories.GuiUtils;
import org.whstsa.library.gui.factories.LibraryManagerUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManagementTables {

    public static StackPane libraryOverviewTable(LibraryDB libraryDB, Table<ILibrary> libraryTable) {
        libraryTable.addColumn("Library Name", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getName()), true, TableColumn.SortType.DESCENDING, 100);
        ObservableReference<List<ILibrary>> observableReference = ObjectDelegate::getLibraries;
        libraryTable.setReference(observableReference);
        libraryTable.getTable().setOnMouseEntered(event -> libraryTable.refresh());
        libraryTable.getTable().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ILibrary selectedLibrary = libraryTable.getSelected();
                if (selectedLibrary == null) {
                    return;
                }
                libraryDB.getInterfaceManager().display(new GuiLibraryManager(selectedLibrary, libraryDB));
            }
        });

        Button newLibraryButton = GuiUtils.createButton("New Library", (event) ->
                LibraryMetaDialogs.createLibrary((library) -> {
                    if (library == null) {
                        return;
                    }
                    Loader.getLoader().loadLibrary(library);
                    libraryTable.refresh();
                })
        );

        Button editLibraryButton = GuiUtils.createButton("Edit Library", (event) -> {
            ILibrary selectedLibrary = libraryTable.getSelected();
            if (selectedLibrary == null) {
                return;
            }
            LibraryMetaDialogs.updateLibrary(selectedLibrary, (library) ->
                    libraryTable.refresh()
            );
        });
        editLibraryButton.setDisable(true);

        Button deleteLibraryButton = GuiUtils.createButton("Delete Library", true, (event) -> {
            ILibrary selectedLibrary = libraryTable.getSelected();
            if (selectedLibrary == null) {
                return;
            }
            LibraryMetaDialogs.deleteLibrary(selectedLibrary, (library) ->
                    libraryTable.refresh()
            );
        });
        deleteLibraryButton.setDisable(true);
        Button openLibraryButton = GuiUtils.createButton("Open Library", true, (event) -> {
            ILibrary selectedLibrary = libraryTable.getSelected();
            if (selectedLibrary == null) {
                return;
            }
            libraryDB.getInterfaceManager().display(new GuiLibraryManager(selectedLibrary, libraryDB));
        });
        openLibraryButton.setDisable(true);

        libraryTable.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editLibraryButton.setDisable(newSelection == null);
            deleteLibraryButton.setDisable(newSelection == null);
            openLibraryButton.setDisable(newSelection == null);
        });

        StackPane libraryButtonContainer = GuiUtils.createSplitPane(GuiUtils.Orientation.VERTICAL, newLibraryButton, editLibraryButton, deleteLibraryButton, openLibraryButton);

        if (Boolean.parseBoolean(libraryDB.getConfig().getProperty("tooltips"))) {
            newLibraryButton.setTooltip(GuiUtils.createToolTip("Create a new library"));
            editLibraryButton.setTooltip(GuiUtils.createToolTip("Edit a library's name"));
            deleteLibraryButton.setTooltip(GuiUtils.createToolTip("Remove a library"));
            openLibraryButton.setTooltip(GuiUtils.createToolTip("View a library in the Library Manager"));
        }

        return GuiUtils.createSplitPane(GuiUtils.Orientation.HORIZONTAL, libraryButtonContainer, libraryTable.getTable());
    }

    public static StackPane personOverviewTable(LibraryDB libraryDB, Table<IPerson> personTable) {
        personTable.addColumn("First Name", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getFirstName()), true, TableColumn.SortType.DESCENDING, 50);
        personTable.addColumn("Last Name", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getLastName()), true, TableColumn.SortType.DESCENDING, 50);
        ObservableReference<List<IPerson>> observableReference = ObjectDelegate::getPeople;
        personTable.setReference(observableReference);
        personTable.getTable().setOnMouseEntered(event -> personTable.refresh());
        personTable.getTable().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                IPerson selectedPerson = personTable.getSelected();
                if (selectedPerson == null) {
                    return;
                }
                PersonMetaDialogs.updatePerson(selectedPerson, person ->
                        personTable.refresh()
                );
            }
        });

        Button newPersonButton = GuiUtils.createButton("New Person", event ->
                PersonMetaDialogs.createPerson(person -> {
                    if (person == null) {
                        return;
                    }
                    personTable.refresh();
                })
        );

        Button editPersonButton = GuiUtils.createButton("Edit Person", event -> {
            IPerson selectedPerson = personTable.getSelected();
            if (selectedPerson == null) {
                return;
            }
            PersonMetaDialogs.updatePerson(selectedPerson, person ->
                    personTable.refresh()
            );
        });
        editPersonButton.setDisable(true);

        Button deletePersonButton = GuiUtils.createButton("Delete Person", true, event -> {
            IPerson selectedPerson = personTable.getSelected();
            if (selectedPerson == null) {
                return;
            }
            PersonMetaDialogs.deletePerson(selectedPerson, person ->
                    personTable.refresh()
            );
        });
        deletePersonButton.setDisable(true);

        personTable.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean disabled = newSelection == null;
            editPersonButton.setDisable(disabled);
            deletePersonButton.setDisable(disabled);
        });

        StackPane personButtonContainer = GuiUtils.createSplitPane(GuiUtils.Orientation.VERTICAL, newPersonButton, editPersonButton, deletePersonButton);

        if (Boolean.parseBoolean(libraryDB.getConfig().getProperty("tooltips"))) {
            newPersonButton.setTooltip(GuiUtils.createToolTip("Create a new person"));
            editPersonButton.setTooltip(GuiUtils.createToolTip("Edit a person's name or role"));
            deletePersonButton.setTooltip(GuiUtils.createToolTip("Remove a person (Must have no memberships or books!)"));
        }

        return GuiUtils.createSplitPane(GuiUtils.Orientation.HORIZONTAL, personTable.getTable(), personButtonContainer);
    }

    public static BorderPane libraryManagerTable(ObservableReference<ILibrary> libraryReference, LibraryDB libraryDB) {

        BorderPane mainContainer = new BorderPane();
        GuiStatusBar statusBar = new GuiStatusBar();
        mainContainer.setBottom(statusBar);
        mainContainer.setTop(new VBox(new MenuBar(), new HBox(), new HBox(), LibraryManagerUtils.createTitleBar(libraryReference.poll().getName() + " Members", "membersTitle")));

        Table<IMember> mainMemberTable = new Table<>();
        memberManagerTable(mainMemberTable, libraryReference);
        TableView<IMember> memberTableView = mainMemberTable.getTable();
        memberTableView.setId("memberTable");
        mainMemberTable.refresh();

        Table<IBook> mainBookTable = new Table<>();
        bookManagerTable(mainBookTable, libraryReference);
        TableView<IBook> bookTableView = mainBookTable.getTable();
        bookTableView.setId("bookTable");
        mainBookTable.refresh();

        MainMenuBar mainMenuBar = new MainMenuBar(mainBookTable, mainMemberTable, libraryReference, null, null, libraryDB, statusBar);
        ((VBox) mainContainer.getTop()).getChildren().set(0, mainMenuBar.getMenu());

        Button back = GuiUtils.createButton("Back to Main Menu", true, event ->
                libraryDB.getInterfaceManager().display(new GuiMain(libraryDB))
        );

        ToggleGroup viewToggleGroup = new ToggleGroup();

        ToggleButton viewMembers = GuiUtils.createToggleButton("Members");
        viewMembers.setUserData(true);
        viewMembers.setToggleGroup(viewToggleGroup);
        viewMembers.setSelected(true);
        ToggleButton viewBooks = GuiUtils.createToggleButton("Books");
        viewBooks.setToggleGroup(viewToggleGroup);
        viewBooks.setUserData(false);
        viewMembers.setDisable(true);

        HBox viewButtons = GuiUtils.createHBox(0, viewMembers, viewBooks);

        Button checkout = GuiUtils.createButton("Checkout", event -> {
            IMember selectedMember = mainMemberTable.getSelected();
            CheckoutMetaDialogs.checkoutMember(member -> {
                mainMemberTable.refresh();
                mainBookTable.refresh();
                viewBooks.setDisable(true);
                viewMembers.setDisable(false);
                viewBooks.setSelected(true);
                viewMembers.setSelected(false);
                statusBar.setSaved(false);

            }, selectedMember, mainContainer, mainBookTable, viewBooks, viewMembers, libraryReference);
            mainMemberTable.getTable().getSelectionModel().clearSelection();
        });
        checkout.setDisable(true);
        checkout.setStyle("-fx-base:#91c4e2;");

        Button checkin = GuiUtils.createButton("Return", event -> {
            IMember selectedMember = mainMemberTable.getSelected();
            CheckoutMetaDialogs.checkinMember(member -> {
                mainMemberTable.refresh();
                mainBookTable.refresh();
                statusBar.setSaved(false);
            }, selectedMember, mainContainer, mainBookTable, viewBooks, viewMembers, libraryReference);
            mainMemberTable.getTable().getSelectionModel().clearSelection();
        });
        checkin.setDisable(true);
        checkin.setStyle("-fx-base: #91c4e2;");

        Label membersLabel = GuiUtils.createLabel("Members", 16);
        Button memberNew = GuiUtils.createButton("Add", event ->
                MemberMetaDialogs.createMember(member -> {
                    mainMemberTable.refresh();
                    statusBar.setSaved(false);
                }, libraryReference)
        );
        Button memberEdit = GuiUtils.createButton("Edit", event -> {
            IMember selectedMember = mainMemberTable.getSelected();
            if (selectedMember == null) {
                return;
            }
            MemberMetaDialogs.updateMember(selectedMember, member -> {
                mainMemberTable.refresh();
                statusBar.setSaved(false);
            });
        });
        memberEdit.setDisable(true);
        Button memberSearch = GuiUtils.createButton("Search", event ->
                GuiUtils.createMemberSearchBar("memberSearch", "Search for Member:", LibraryManagerUtils.getMemberNames(libraryReference.poll()), mainContainer, libraryReference, mainMemberTable)
        );
        Button memberDelete = GuiUtils.createButton("Remove", event ->
                MemberMetaDialogs.deleteMember(mainMemberTable.getSelected(), member -> {
                    if (member == null) {
                        return;
                    }
                    mainMemberTable.refresh();
                    statusBar.setSaved(false);
                })
        );
        memberDelete.setDisable(true);


        Label booksLabel = GuiUtils.createLabel("Books", 16);
        Button bookAdd = GuiUtils.createButton("Add", event ->
                BookMetaDialogs.createBook(book -> {
                    if (book == null) {
                        return;
                    }
                    Loader.getLoader().loadBook(book);
                    mainBookTable.refresh();
                    statusBar.setSaved(false);
                }, libraryReference)
        );
        Button bookEdit = GuiUtils.createButton("Edit", event -> {
            IBook selectedBook = mainBookTable.getSelected();
            if (selectedBook == null) {
                return;
            }
            BookMetaDialogs.updateBook(selectedBook, book -> {
                mainBookTable.refresh();
                statusBar.setSaved(false);
            }, libraryReference);
        });
        bookEdit.setDisable(true);
        Button bookDelete = GuiUtils.createButton("Remove", event -> {
            IBook selectedBook = mainBookTable.getSelected();
            if (selectedBook == null) {
                return;
            }
            BookMetaDialogs.deleteBook(selectedBook, book -> {
                mainBookTable.refresh();
                statusBar.setSaved(false);
            });
        });
        bookDelete.setDisable(true);
        Button bookSearch = GuiUtils.createButton("Search", event ->
                GuiUtils.createBookSearchBar("bookSearch", "Search for Book:", LibraryManagerUtils.getMemberNames(libraryReference.poll()), mainContainer, libraryReference, mainBookTable)
        );

        mainMemberTable.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (((VBox) mainContainer.getTop()).getChildren().get(1).getId() != null) {
                if (((VBox) mainContainer.getTop()).getChildren().get(1).getId().equals("toolbar") || newSelection == null) {
                    checkout.setDisable(true);
                    checkin.setDisable(true);
                } else {
                    checkout.setDisable(false);
                    checkin.setDisable(false);
                }
            } else {
                checkout.setDisable(false);
                checkin.setDisable(false);
            }

            checkout.setDisable(newSelection == null);
            checkin.setDisable(newSelection == null);
            memberEdit.setDisable(newSelection == null);
            memberDelete.setDisable(newSelection == null);
        });

        mainBookTable.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            bookEdit.setDisable(newSelection == null);
            bookDelete.setDisable(newSelection == null);
        });

        VBox buttonGroup = GuiUtils.createVBox(15, back,
                GuiUtils.createSeparator(), viewButtons,
                GuiUtils.createSeparator(), membersLabel, checkout, checkin, GuiUtils.createSeparator(), memberNew, memberEdit, memberSearch, memberDelete,
                GuiUtils.createSeparator(), booksLabel, bookAdd, bookEdit, bookSearch, bookDelete);
        buttonGroup.setSpacing(5.0);

        mainContainer.setLeft(buttonGroup);
        mainContainer.setCenter(memberTableView);
        viewToggleGroup.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if (new_toggle != null) {
                if ((boolean) viewToggleGroup.getSelectedToggle().getUserData()) {
                    LibraryDB.LOGGER.debug("Switching to Member table");
                    mainContainer.setCenter(memberTableView);
                    ((VBox) mainContainer.getTop()).getChildren().set(3, LibraryManagerUtils.createTitleBar(libraryReference.poll().getName() + " Members", "membersTitle"));
                    viewMembers.setDisable(true);
                    viewBooks.setDisable(false);
                    mainBookTable.getTable().getSelectionModel().clearSelection();

                } else {
                    LibraryDB.LOGGER.debug("Switching to Book table");
                    mainContainer.setCenter(bookTableView);
                    ((VBox) mainContainer.getTop()).getChildren().set(3, LibraryManagerUtils.createTitleBar(libraryReference.poll().getName() + " Books", "booksTitle"));
                    viewMembers.setDisable(false);
                    viewBooks.setDisable(true);
                    mainMemberTable.getTable().getSelectionModel().clearSelection();
                }
            }
        });

        BackgroundWorker.getBackgroundWorker().registerOperation(() -> {//checks if both buttons are disabled at the same time and re-enables them, fixing annoying issues
            if (viewBooks.isDisabled() && viewMembers.isDisabled() && !((VBox) mainContainer.getTop()).getChildren().get(1).getId().equals("toolbar")) {
                viewMembers.setDisable(false);
                viewBooks.setDisable(false);
            }
            switch (mainContainer.getCenter().getId()) {
                case "memberTable":
                    bookSearch.setDisable(true);
                    memberSearch.setDisable(false);
                    break;
                case "bookTable":
                    bookSearch.setDisable(false);
                    memberSearch.setDisable(true);
                    break;
                default:
                    bookSearch.setDisable(true);
                    memberSearch.setDisable(true);
                    break;
            }
        });

        BackgroundWorker.getBackgroundWorker().registerOperation(() -> {//Disables checkout/in buttons when in checkout/in interface
            if (mainContainer.getCenter().getId().equals("memberTable") && !((VBox) mainContainer.getTop()).getChildren().get(3).getId().equals("membersTitle")) {
                ((VBox) mainContainer.getTop()).getChildren().set(3, LibraryManagerUtils.createTitleBar(libraryReference.poll().getName() + " Members", "membersTitle"));
            }
            if (mainContainer.getCenter().getId().equals("bookTable") && !((VBox) mainContainer.getTop()).getChildren().get(3).getId().equals("booksTitle")) {
                ((VBox) mainContainer.getTop()).getChildren().set(3, LibraryManagerUtils.createTitleBar(libraryReference.poll().getName() + " Books", "booksTitle"));
            }
        });

        if (Boolean.parseBoolean(libraryDB.getConfig().getProperty("tooltips"))) {
            LibraryManagerUtils.addTooltip(mainMemberTable.getTable(), "Double-click to see a member's checked-out books.");
            LibraryManagerUtils.addTooltip(mainBookTable.getTable(), "Double-click to see the status of the copies of this book.");
            back.setTooltip(GuiUtils.createToolTip("Return to the main menu"));
            viewMembers.setTooltip(GuiUtils.createToolTip("View the Members table"));
            viewMembers.setTooltip(GuiUtils.createToolTip("View the Books table"));
            checkout.setTooltip(GuiUtils.createToolTip("Checkout books to a member"));
            checkin.setTooltip(GuiUtils.createToolTip("Return a member's library books"));
            memberNew.setTooltip(GuiUtils.createToolTip("Add a new member to the library"));
            memberEdit.setTooltip(GuiUtils.createToolTip("Edit a member's name and role"));
            memberDelete.setTooltip(GuiUtils.createToolTip("Remove a member from the library (Must have no books!)"));
            memberSearch.setTooltip(GuiUtils.createToolTip("Filter through library members"));
            bookAdd.setTooltip(GuiUtils.createToolTip("Add a book to the library"));
            bookEdit.setTooltip(GuiUtils.createToolTip("Edit a book's title or author"));
            bookDelete.setTooltip(GuiUtils.createToolTip("Remove book from library (All copies must be in stock!)"));
            bookSearch.setTooltip(GuiUtils.createToolTip("Filter through library books"));
        }

        return mainContainer;
    }

    private static void memberManagerTable(Table<IMember> mainTable, ObservableReference<ILibrary> libraryReference) {
        mainTable.addColumn("First Name", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getPerson().getFirstName()), true, TableColumn.SortType.DESCENDING, 100);
        mainTable.addColumn("Last Name", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getPerson().getLastName()), true, TableColumn.SortType.DESCENDING, 100);
        mainTable.addColumn("Role", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getPerson().isTeacher() ? "Teacher" : "Student"), true, TableColumn.SortType.DESCENDING, 50);
        mainTable.addColumn("Fines", (cellData) -> new ReadOnlyStringWrapper("$" + cellData.getValue().getFine() + "0"), true, TableColumn.SortType.DESCENDING, 25);
        mainTable.addColumn("Books", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getCheckouts().size() + ""), true, TableColumn.SortType.DESCENDING, 25);
        ObservableReference<List<IMember>> observableReference = () -> libraryReference.poll().getMembers();
        mainTable.setReference(observableReference);
        mainTable.getTable().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && mainTable.getSelected() != null) {
                LibraryDB.LOGGER.debug("Listing member's books");
                MemberMetaDialogs.listBooks(mainTable.getSelected());
            }
        });
    }

    private static void bookManagerTable(Table<IBook> mainTable, ObservableReference<ILibrary> libraryReference) {
        mainTable.addColumn("Title", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getName()), true, TableColumn.SortType.DESCENDING, 200);
        mainTable.addColumn("Author", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getAuthorName()), true, TableColumn.SortType.DESCENDING, 100);
        mainTable.addColumn("Genre", (cellData) -> new ReadOnlyStringWrapper(cellData.getValue().getType().getGenre()), true, TableColumn.SortType.DESCENDING, 50);
        mainTable.addColumn("Copies", (cellData) -> new ReadOnlyStringWrapper(libraryReference.poll().getQuantity(cellData.getValue().getID()) + ""), true, TableColumn.SortType.DESCENDING, 25);
        mainTable.addColumn("Checked out", (cellData) -> {
            ILibrary library = libraryReference.poll();
            List<ICheckout> checkouts = library.getCheckouts().get(cellData.getValue());
            boolean isCheckedOut = checkouts != null && checkouts.size() == library.getQuantity(cellData.getValue().getID());
            return new ReadOnlyStringWrapper(isCheckedOut ? "True" : "False");
        }, true, TableColumn.SortType.DESCENDING, 30);
        mainTable.addColumn("Due Date", (cellData) -> {
            ILibrary library = libraryReference.poll();
            List<ICheckout> checkouts = library.getCheckouts().get(cellData.getValue());
            boolean hasBeenCheckedOut = checkouts != null;
            if (hasBeenCheckedOut) {
                DateFormat formattedDate = new SimpleDateFormat("MM/dd/yyyy");
                // Sorts the checkouts by date to get the nearest due date
                List<ICheckout> sortedCheckouts = checkouts.stream()
                        .sorted(Comparator.comparing(ICheckout::getDueDate))
                        .collect(Collectors.toList());
                // Display N/A if there are no checkouts
                if (sortedCheckouts.size() == 0 || sortedCheckouts.get(0) == null) {
                    return new ReadOnlyStringWrapper("N/A");
                }
                ICheckout checkout = sortedCheckouts.get(0);
                Date nearestDate = checkout.getDueDate();
                return new ReadOnlyStringWrapper((formattedDate.format(nearestDate) + (checkouts.size() > 1 ? "..." : "") + (checkout.isOverdue() ? "o" : "")));
                //Return date and add "..." if more than one book is checked out
                //the "o" addition indicates to the cellFactory that the book is overdue, and that the date should be displayed in red. The "o" is removed by cell factory
            }
            return new ReadOnlyStringWrapper("N/A");
        }, true, TableColumn.SortType.DESCENDING, 40);
        ILibrary library = libraryReference.poll();

        TableColumn<IBook, String> dateColumn = (TableColumn<IBook, String>) mainTable.getTable().getColumns().get(5);
        dateColumn.setCellFactory(param -> new TableCell<IBook, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                if (!(item == null) || !empty) {
                    setTextFill(item.contains("o") ? Color.RED : Color.GREEN);
                    setText(item.replace("o", ""));
                    setTooltip(GuiUtils.createToolTip("Due date of a book. Green if not overdue, red if past due. \nDouble-click to see info for each copy"));
                    if (item.equals("N/A")) {
                        setTextFill(Color.BLACK);
                    }
                } else {
                    setTextFill(Color.BLACK);//If cell has no content, leave it blank (Omitting this caused the repeating date issue)
                    setText("");
                }
            }
        });

        ObservableReference<List<IBook>> observableReference = () -> libraryReference.poll().getBooks();
        mainTable.setReference(observableReference);
        mainTable.getTable().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && mainTable.getSelected() != null) {
                LibraryDB.LOGGER.debug("Listing member's books");
                BookMetaDialogs.listCopies(mainTable.getSelected(), libraryReference);
            }
        });
    }

}
