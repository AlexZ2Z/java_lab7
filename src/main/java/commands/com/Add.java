package commands.com;

import SQLutil.SQLManager;
import commands.CommandAbstract;
import commands.ElementInput;
import data.City;
import exception.ArgumentException;
import exception.CommandException;
import util.Manager;
import util.Reply;
import util.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class Add extends CommandAbstract {
    private final String name = "add";

    @Override
    public List<Object> checkArguments(Scanner scanner, int mode) throws ArgumentException {
        if (getArgList().size() != 0) {

            throw new ArgumentException("Аргумент введен неверно!");
        }
        City newCity = ElementInput.getNewElement(scanner, mode);
        if (newCity == null) {
            throw new ArgumentException("Ошибка в файле");
        }
        getArgList().add(newCity);
        return getArgList();
    }

    @Override
    public boolean getNewEl() {
        return true;
    }

    @Override
    public Reply execute(Manager manager, User user) throws CommandException{
        City newCity = (City) getArgList().get(0);
        try {
        newCity.setId(SQLManager.getNextId());
        newCity.setUsername(user.getUsername());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        synchronized (manager.getCollectionManager().getCollection()) {
            for (City city : manager.getCollectionManager().getCollection()) {
                if (newCity.compareTo(city) == 0) {
                    throw new CommandException("Элемент не добавленн т.к. элемент эквивкелентный этому уже есть!");
                }
            }
        }
        try {
            SQLManager.add(newCity, getUser());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new CommandException("Ошибка при каботе с БД!, элемент не добавлен");
        }

        synchronized (manager.getCollectionManager().getCollection()) {
            manager.getCollectionManager().getCollection().add(newCity);
        }
        return new Reply("Элемент добавлен!");
    }

    @Override
    public String toString() {
        return "add";
    }
}
