package com.gridnine.testing.filter;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

/**
 * Фабрика для управления и создания экземпляров интерфейса {@link  Filter}.
 * Этот класс предоставляет функциональность для автоматического обнаружения и
 * регистрации реализаций {@code Filter} из указанного (или дефолтного) пакета
 */
public class FilterFactory {

    /**
     * Карта представляет собой контейнер зарегистрированных фильтров этой фабрики. Все автоматически
     * (или вручную) зарегистрированные фильтры помещаются в эту карту
     */
    private final Map<String, Filter> filtersMap = new HashMap<>();

    /**
     * Поле, хранящее полный путь до пакета (в строковом представлении),
     * в котором следует искать фильтры
     */
    private final String packageToScan;

    /**
     * Создает новый экземпляр фабрики фильтров с пакетом по умолчанию.
     * Пакет по умолчанию является пакетом, в котором находится этот класс.
     * Этот конструктор удобен для использования, когда фильтры находятся в том же пакете, что и фабрика.
     */
    public FilterFactory() {
        this.packageToScan = this.getClass().getPackageName();
    }

    /**
     * Создает новый экземпляр фабрики фильтров с указанным пакетом для сканирования.
     * Этот конструктор позволяет указать пакет, в котором будут сканироваться фильтры.
     * Переданный пакет должен быть валидным, существующим пакетом, доступным для чтения.
     * Путь должен начинаться от директории "src" (включительно), вложенные директории
     * должны быть разделены символом {@code /} (косая черта), номер в UTF-8: 0x2F
     *
     * @param packageToScan пакет, в котором будут сканироваться фильтры
     * @throws IllegalArgumentException если этот покет невозможно использовать
     */
    public FilterFactory(String packageToScan) {
        if (packageToScan == null || packageToScan.isBlank()) {
            throw new IllegalArgumentException("PackageToScan cannot be null or blank");
        }

        try {
            Path path = Path.of(packageToScan);
            if (Files.exists(path) && Files.isDirectory(path) && Files.isReadable(path)) {
                this.packageToScan = packageToScan;
                return;
            }
        } catch (InvalidPathException | SecurityException _) { }

        throw new IllegalArgumentException("Package " + packageToScan + " cannot be used");
    }

    /**
     * Возвращает экземпляр {@code Filter} по его имени, определенном в методе {@code getName()}. Этот метод
     * намеренно не пытается зарегистрировать фильтр по его имени в случая его отсутствия, чтобы иметь более
     * точный контроль над контейнером фильтров. Так же этот метод можно использовать для проверки наличия
     * фильтра в контейнере с помощью {@code filterFactory.getByName(filterName) == null}
     *
     * @param name имя фильтра, экземпляр которого нужно получить
     * @return экземпляр {@code Filter}
     * @throws NullPointerException если под запрошенным именем не зарегистрирован фильтр
     * @see Filter#getName()
     */
    public Filter getByName(String name) {
        Filter filter = filtersMap.get(name);
        Objects.requireNonNull(filter, "Filter is not found: " + name + ". Please register this filter.");
        return filter;
    }

    /**
     * Возвращает список зарегистрированных объектов, чьи классы реализуют интерфейс {@link Filter}.
     * Если список фильтров пуст, метод автоматически попытается зарегистрировать все фильтры из пакета,
     * переданного в конструктор при создании фабрики, если ни один фильтр не был обнаружен, то возвращается
     * пустой список. Регистрация фильтров осуществляется путем сканирования файлов классов в пакете и создания
     * экземпляров классов, реализующих интерфейс {@link Filter}.
     *
     * @return список зарегистрированных фильтров
     */
    public Collection<Filter> getFilters() {
        if (filtersMap.isEmpty()) {
            registerFilters();
        }

        return new ArrayList<>(filtersMap.values());
    }

    /**
     * Ручная регистрация фильтров по их экземплярам. Метод позволяет зарегистрировать
     * один или несколько фильтров вручную через сами объекты этих классов.
     * Если переданный фильтр уже зарегистрирован в контейнере, то он будет проигнорирован
     *
     * @param filters фильтры, которые необходимо зарегистрировать
     * @throws NullPointerException если один из фильтров равен null
     */
    public void registerManually(Filter... filters) {
        Arrays.stream(filters)
                .peek(filter -> Objects.requireNonNull(filter, "Filters cannot be null"))
                .filter(filter -> !filtersMap.containsKey(filter.getName()))
                .forEach(filter -> filtersMap.put(filter.getName(), filter));
    }

    /**
     * Регистрирует фильтры вручную по их именам.
     * Этот метод позволяет зарегистрировать один или несколько фильтров по их строковым именам.
     * Для каждого указанного имени метод проверяет, что фильтр с таким именем еще не был
     * зарегистрирован. Если фильтр с таким именем не был найден, метод пытается создать
     * экземпляр фильтра с помощью метода {@link #getValidFilter(String)}.
     *
     * @param names Массив строковых имен фильтров, которые нужно зарегистрировать
     * @throws NullPointerException     Если переданное имя фильтра равно null.
     * @throws IllegalArgumentException Если фильтр с указанным именем не найден.
     */
    public void registerManuallyByName(String... names) {
        Arrays.stream(names)
                .peek(name -> Objects.requireNonNull(name, "Filters cannot be null"))
                .filter(name -> !filtersMap.containsKey(name))
                .forEach(name -> filtersMap.put(name, getValidFilter(name)));
    }

    /**
     * Регистрирует фильтры, найденные в пакете {@link FilterFactory#packageToScan}.
     * Метод сканирует указанный пакет на наличие классов, реализующих интерфейс {@link Filter}.
     * Для каждого найденного класса будет предпринята попытка создать экземпляр фильтра. В случае
     * неудачи, метод просто переходит к следующему классу. Успешно созданные фильтры регистрируются
     * в контейнере фильтров.
     *
     * @see Filter
     */
    public void registerFilters() {

        Optional.ofNullable(this.getClass().getClassLoader().getResource(packageToScan.replace(".", "/")))
                .map(url -> new File(url.getFile()))
                .filter(file -> file.exists() && file.isDirectory())
                .ifPresent(directory ->
                        Arrays.stream(directory.listFiles()).forEach(file ->

                                tryToGetClass(file)
                                        .flatMap(this::tryFilterInstanced)
                                        .ifPresent(this::registerManually)
                        ));
    }

    private Filter getValidFilter(String name) {
        Class<?> aClass;
        IllegalArgumentException ifNotFound = new IllegalArgumentException("Filter " + name + " is not found");

        try {
            aClass = Class.forName(name);   // Попытка найти фильтр без пакета/с переданным пакетом
        } catch (ClassNotFoundException _) {
            aClass = this.tryToGetClass(new File(name)).orElseThrow(() -> ifNotFound);   // Попытка найти фильтр с пакетом из конструктора
        }
        return tryFilterInstanced(aClass).orElseThrow(() -> ifNotFound);
    }

    private Optional<Filter> tryFilterInstanced(Class<?> aClass) {
        try {
            if (!aClass.isInterface() && !aClass.isEnum()
                    && aClass.getDeclaredConstructor().newInstance() instanceof Filter filter) {
                return Optional.of(filter);
            }
        } catch (ReflectiveOperationException _) { }   // Не удалось найти конструктор и создать объект класса aClass
        return Optional.empty();
    }

    private Optional<Class<?>> tryToGetClass(File file) {
        try {

            String className = packageToScan + "." + file.getName().replace(".class", "");
            return Optional.of(Class.forName(className));

        } catch (ClassNotFoundException ex) {   // Не удалось найти класс в пакете packageToScan в указанном file
            return Optional.empty();
        }
    }

}
