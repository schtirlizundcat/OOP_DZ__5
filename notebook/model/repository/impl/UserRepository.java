package notebook.model.repository.impl;


import notebook.util.mapper.impl.UserMapper;
import notebook.model.User;
import notebook.model.repository.GBRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static notebook.util.DBConnector.DB_PATH;

public class UserRepository implements GBRepository {
    private final UserMapper mapper;
    // private final String fileName = new String();

        //private final FileOperation operation;

    public UserRepository() {
        try (FileWriter writer = new FileWriter(DB_PATH, true)) {
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        mapper = new UserMapper();
    }
//    public UserRepository(FileOperation operation) {
//        try (FileWriter writer = new FileWriter(fileName, true)) {
//            writer.flush();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        this.mapper = new UserMapper();
//        this.operation = operation;
//    }

    @Override
    public List<User> findAll() {
        List<String> lines = readAll();
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            users.add(mapper.toOutput(line));
        }
        return users;
    }

    @Override
    public User create(User user) {
        List<User> users = findAll();
        long max = 0L;
        for (User u : users) {
            long id = u.getId();
            if (max < id){
                max = id;
            }
        }
        long next = max + 1;
        user.setId(next);
        users.add(user);
        write(users);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
        User editUser = users.stream()
                .filter(u -> u.getId()
                        .equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found"));
        if (!update.getFirstName().isEmpty()) {
            editUser.setFirstName(update.getFirstName());
        }
        if (!update.getLastName().isEmpty()) {
            editUser.setLastName(update.getLastName());
        }
        if (!update.getPhone().isEmpty()) {
            editUser.setPhone(update.getPhone());
        }
        write(users);
        return Optional.of(update);
    }

    @Override
    public boolean delete(Long id) {
        boolean result;
        List<String> lines = new ArrayList<>();
        lines = readAll();
        result = false;
        try {
            // System.out.println("НАЧАЛО Удаление пользователя.");
            // System.out.println("Размер списка пользователей: " + lines.size() + ". Запрошено удаление пользователя с идентификатором (Long) " + id);
            int indexToBeRemoved = Long.valueOf(id).intValue();
            System.out.println("Размер списка пользователей: " + lines.size() + ". Запускаем удаление пользователя с идентификатором (int) " + indexToBeRemoved);
            lines.remove(indexToBeRemoved - 1);
            System.out.println("Размер списка пользователей: " + lines.size() + ".");
            // System.out.println("Записываем в файловую базу.");
            saveAll(lines);
            // System.out.println("Готов. КОНЕЦ Удаление пользователя.");
            result = true;
        }
        catch (Exception e) {};
        return result;
    }

    private void write(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u: users) {
            lines.add(mapper.toInput(u));
        }
        saveAll(lines);
    }

    public List<String> readAll() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(DB_PATH);
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            if (line != null) {
                lines.add(line);
            }
            while (line != null) {
                // считываем остальные строки в цикле
                line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void saveAll(List<String> data) {
        try (FileWriter writer = new FileWriter(DB_PATH, false)) {
            for (String line : data) {
                // запись всей строки
                writer.write(line);
                // запись по символам
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
