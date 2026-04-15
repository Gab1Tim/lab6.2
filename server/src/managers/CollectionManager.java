package server.managers;

import com.google.gson.Gson;
import common.models.Organization;
import common.models.OrganizationType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Управляет коллекцией организаций (загрузка/операции/сохранение).
 */
public class CollectionManager {

    private LinkedHashMap<Integer, Organization> collections;
    private LocalDateTime creationDate;
    private FileManager fileManager;
    private WalManager walManager;
    private final Gson gson = new Gson();

    /**
     * Создаёт менеджер и загружает данные.
     */
    public CollectionManager(String fileName) {
        fileManager = new FileManager(fileName);
        walManager = new WalManager(fileName + ".wal");
        creationDate = LocalDateTime.now();

        if (fileManager.tempFileExists() && fileManager.isMainFileAccessible()) {
            System.out.println("Temp file and main file both found. Merging...");
            collections = fileManager.loadAndMerge();
            System.out.println("Merge complete.");
        } else if (fileManager.tempFileExists()) {
            System.out.println("Main file not found. Loading from temp file...");
            collections = fileManager.loadFromTemp();
        } else {
            collections = fileManager.load();
        }

        if (collections == null) {
            collections = new LinkedHashMap<>();
        }

        long maxId = collections.values().stream()
                .map(Organization::getId)
                .filter(id -> id != null)
                .max(Long::compareTo)
                .orElse(0L);

        Organization.setNextId(maxId + 1);

        if (walManager.exists()) {
            System.out.println("WAL log found. Recovering...");
            replayWal();
            System.out.println("Recovery complete.");
        }
    }

    /**
     * Восстанавливает состояние из WAL.
     */
    private void replayWal() {
        for (String line : walManager.readLog()) {
            try {
                if (line.startsWith("INSERT ")) {
                    String[] parts = line.substring(7).split(" ", 2);
                    Integer key = Integer.parseInt(parts[0]);
                    Organization org = gson.fromJson(parts[1], Organization.class);

                    if (!collections.containsKey(key)) {
                        collections.put(key, org);
                    }

                } else if (line.startsWith("REMOVE ")) {
                    Integer key = Integer.parseInt(line.substring(7).trim());
                    collections.remove(key);

                } else if (line.startsWith("UPDATE ")) {
                    String[] parts = line.substring(7).split(" ", 2);
                    Long id = Long.parseLong(parts[0]);
                    Organization orgFromWal = gson.fromJson(parts[1], Organization.class);

                    Integer keyToUpdate = collections.entrySet().stream()
                            .filter(entry -> entry.getValue().getId().equals(id))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);

                    if (keyToUpdate != null) {
                        Organization oldOrg = collections.get(keyToUpdate);

                        Organization updatedOrg = new Organization(
                                oldOrg.getId(),
                                orgFromWal.getName(),
                                orgFromWal.getCoordinates(),
                                oldOrg.getCreationDate(),
                                orgFromWal.getAnnualTurnover(),
                                orgFromWal.getType(),
                                orgFromWal.getOfficialAddress()
                        );

                        collections.put(keyToUpdate, updatedOrg);
                    }

                } else if (line.equals("CLEAR")) {
                    collections.clear();

                } else if (line.startsWith("REMOVE_GREATER_KEY ")) {
                    Integer refKey = Integer.parseInt(line.substring(19).trim());
                    collections.entrySet().removeIf(e -> e.getKey() > refKey);

                } else if (line.startsWith("REMOVE_LOWER ")) {
                    Organization ref = gson.fromJson(line.substring(13), Organization.class);
                    collections.entrySet().removeIf(e -> e.getValue().compareTo(ref) < 0);
                }
            } catch (Exception e) {
                System.out.println("WAL replay error: " + line + " -> " + e.getMessage());
            }
        }
    }

    /**
     * Добавляет организацию по ключу.
     */
    public void insert(Integer key, Organization organization) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null");
        }
        if (collections.containsKey(key)) {
            throw new IllegalArgumentException("Key already exists");
        }

        Organization serverOrganization = new Organization(
                organization.getName(),
                organization.getCoordinates(),
                organization.getAnnualTurnover(),
                organization.getType(),
                organization.getOfficialAddress()
        );

        walManager.log("INSERT " + key + " " + gson.toJson(serverOrganization));
        collections.put(key, serverOrganization);
    }

    /**
     * Удаляет элемент по ключу.
     */
    public void remove(Integer key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (!collections.containsKey(key)) throw new IllegalArgumentException("Key does not exist");

        walManager.log("REMOVE " + key);
        collections.remove(key);
    }

    /**
     * Проверяет наличие ключа.
     */
    public boolean containsKey(Integer key) {
        return collections.containsKey(key);
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        walManager.log("CLEAR");
        collections.clear();
    }

    /**
     * Обновляет организацию по id.
     */
    public void update(Long id, Organization newOrg) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        if (newOrg == null) throw new IllegalArgumentException("Organization cannot be null");

        Integer keyToUpdate = collections.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(id))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Organization with id " + id + " does not exist"));

        Organization oldOrg = collections.get(keyToUpdate);

        Organization updatedOrg = new Organization(
                oldOrg.getId(),
                newOrg.getName(),
                newOrg.getCoordinates(),
                oldOrg.getCreationDate(),
                newOrg.getAnnualTurnover(),
                newOrg.getType(),
                newOrg.getOfficialAddress()
        );

        walManager.log("UPDATE " + id + " " + gson.toJson(updatedOrg));
        collections.put(keyToUpdate, updatedOrg);
    }

    /**
     * Удаляет элементы с ключом больше заданного.
     */
    public void removeGreaterKey(Integer referenceKey) {
        if (referenceKey == null) {
            throw new IllegalArgumentException("Reference key cannot be null");
        }

        walManager.log("REMOVE_GREATER_KEY " + referenceKey);
        collections.entrySet().removeIf(e -> e.getKey() > referenceKey);
    }

    /**
     * Удаляет элементы меньше заданного и возвращает их количество.
     */
    public int removeLower(Organization reference) {
        if (reference == null) {
            throw new IllegalArgumentException("Reference organization cannot be null");
        }

        walManager.log("REMOVE_LOWER " + gson.toJson(reference));

        List<Integer> keysToRemove = collections.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(reference) < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        keysToRemove.forEach(collections::remove);
        return keysToRemove.size();
    }

    /**
     * @return организация с минимальным именем (или null)
     */
    public Organization getMinByName() {
        return collections.values().stream()
                .min(Comparator.comparing(Organization::getName))
                .orElse(null);
    }

    /**
     * @return текущая коллекция
     */
    public LinkedHashMap<Integer, Organization> getCollection() {
        return collections;
    }

    /**
     * Возвращает информацию о коллекции в виде строки.
     */
    public String getInfoData() {
        return "Collection type: " + collections.getClass().getSimpleName() + "\n" +
                "Creation date: " + creationDate + "\n" +
                "Number of elements: " + collections.size();
    }

    /**
     * Возвращает содержимое коллекции в виде строки.
     */
    public String getShowData() {
        if (collections.isEmpty()) {
            return "Collection is empty";
        }

        return collections.entrySet().stream()
                .map(entry -> "Key: " + entry.getKey() + " | " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Возвращает организации с типом больше заданного.
     */
    public List<Organization> getFilteredGreaterThanType(OrganizationType referenceType) {
        if (referenceType == null) {
            throw new IllegalArgumentException("Reference type cannot be null");
        }

        return collections.values().stream()
                .filter(org -> org.getType() != null)
                .filter(org -> org.getType().ordinal() > referenceType.ordinal())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает уникальные значения annualTurnover.
     */
    public Set<Integer> getUniqueAnnualTurnovers() {
        return collections.values().stream()
                .map(Organization::getAnnualTurnover)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Сохраняет коллекцию в основной файл.
     */
    public boolean saveToMain() {
        boolean saved = fileManager.saveToMain(collections);
        if (saved) {
            walManager.clear();
        }
        return saved;
    }

    /**
     * Сохраняет коллекцию во временный файл.
     */
    public void saveToTemp() {
        fileManager.saveToTemp(collections);
    }
    public void saveSafely() {
        boolean saved = saveToMain();
        if (!saved) {
            saveToTemp();
        }
    }

    }



