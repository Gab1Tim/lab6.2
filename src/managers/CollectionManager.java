package managers;

import com.google.gson.Gson;
import models.Organization;
import models.OrganizationType;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

        if (collections == null) collections = new LinkedHashMap<>();


        long maxId = 0;
        for (Organization org : collections.values()) {
            if (org.getId() != null && org.getId() > maxId) maxId = org.getId();
        }
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
                    if (!collections.containsKey(key)) collections.put(key, org);
                } else if (line.startsWith("REMOVE ")) {
                    Integer key = Integer.parseInt(line.substring(7).trim());
                    collections.remove(key);
                } else if (line.startsWith("UPDATE ")) {
                    String[] parts = line.substring(7).split(" ", 2);
                    Long id = Long.parseLong(parts[0]);
                    Organization org = gson.fromJson(parts[1], Organization.class);
                    for (Map.Entry<Integer, Organization> entry : collections.entrySet()) {
                        if (entry.getValue().getId().equals(id)) {
                            collections.put(entry.getKey(), org);
                            break;
                        }
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
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (organization == null) throw new IllegalArgumentException("Organization cannot be null");
        if (collections.containsKey(key)) throw new IllegalArgumentException("Key already exists");
        walManager.log("INSERT " + key + " " + gson.toJson(organization));
        collections.put(key, organization);
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
     * Печатает информацию о коллекции.
     */
    public void info() {
        System.out.println("Collection type: " + collections.getClass().getSimpleName());
        System.out.println("Creation date: " + creationDate);
        System.out.println("Number of elements: " + collections.size());
    }

    /**
     * Печатает содержимое коллекции в консоль.
     */
    public void show() {
        if (collections.isEmpty()) {
            System.out.println("Collection is empty");
            return;
        }
        for (Map.Entry<Integer, Organization> entry : collections.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " | " + entry.getValue());
        }
    }

    /**
     * Обновляет организацию по id.
     */
    public void update(Long id, Organization newOrg) {
        boolean found = false;
        for (Map.Entry<Integer, Organization> entry : collections.entrySet()) {
            if (entry.getValue().getId().equals(id)) {
                walManager.log("UPDATE " + id + " " + gson.toJson(newOrg));
                collections.put(entry.getKey(), newOrg);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Organization with id " + id + " does not exist");
    }

    /**
     * Удаляет элементы с ключом больше заданного.
     */
    public void removeGreaterKey(Integer referenceKey) {
        walManager.log("REMOVE_GREATER_KEY " + referenceKey);
        collections.entrySet().removeIf(e -> e.getKey() > referenceKey);
    }

    /**
     * @return организация с минимальным именем (или {@code null})
     */
    public Organization getMinByName() {
        if (collections.isEmpty()) return null;
        Organization minOrg = null;
        for (Organization org : collections.values()) {
            if (minOrg == null || org.getName().compareTo(minOrg.getName()) < 0) minOrg = org;
        }
        return minOrg;
    }

    /**
     * Печатает организации с типом больше заданного.
     */
    public void filterGreaterThanType(OrganizationType referenceType) {
        boolean found = false;
        for (Organization org : collections.values()) {
            if (org.getType() != null && org.getType().ordinal() > referenceType.ordinal()) {
                System.out.println(org);
                found = true;
            }
        }
        if (!found) System.out.println("No organizations found with type greater than " + referenceType);
    }

    /**
     * Печатает уникальные значения annualTurnover.
     */
    public void printUniqueAnnualTurnover() {
        if (collections.isEmpty()) {
            System.out.println("Collection is empty!");
            return;
        }
        HashSet<Integer> uniqueTurnovers = new HashSet<>();
        for (Organization org : collections.values()) uniqueTurnovers.add(org.getAnnualTurnover());
        System.out.println("Unique annual turnover values:");
        for (Integer turnover : uniqueTurnovers) System.out.println(turnover);
    }

    /**
     * @return текущая коллекция
     */
    public LinkedHashMap<Integer, Organization> getCollection() {
        return collections;
    }

    /**
     * Удаляет элементы меньше заданного и возвращает их количество.
     */
    public int removeLower(Organization reference) {
        walManager.log("REMOVE_LOWER " + gson.toJson(reference));
        Iterator<Map.Entry<Integer, Organization>> iterator = collections.entrySet().iterator();
        int removedCount = 0;
        while (iterator.hasNext()) {
            Organization current = iterator.next().getValue();
            if (current.compareTo(reference) < 0) {
                iterator.remove();
                removedCount++;
            }
        }
        return removedCount;
    }

    /**
     * Сохраняет коллекцию и очищает WAL.
     */
    public void save() {
        fileManager.save(collections);
        walManager.clear();
    }

}