package com.gridnine.testing.filter;

import java.io.File;
import java.util.*;

/**
 * Фабрика для управления и создания экземпляров интерфейса {@link  Filter}.
 * Этот класс предоставляет функциональность для автоматического обнаружения и
 * регистрации реализаций {@code Filter} из указанного (или дефолтного) пакета
 */
public class FilterFactory {

    private final Map<String, Filter> filtersMap = new HashMap<>();
    private final String packageToScan;

    /**
     * Создает новый экземпляр фабрики фильтров с указанным пакетом для сканирования.
     * Этот конструктор позволяет указать пакет, в котором будут сканироваться фильтры.
     * Если пакет не существует или не содержит фильтров, фабрика будет пустой.
     *
     * @param packageToScan пакет, в котором будут сканироваться фильтры
     */
    public FilterFactory(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    /**
     * Создает новый экземпляр фабрики фильтров с пакетом по умолчанию.
     * Пакет по умолчанию является пакетом, в котором находится этот класс.
     * Этот конструктор удобен для использования, когда фильтры находятся в том же пакете, что и фабрика.
     */
    public FilterFactory() {
        this.packageToScan = this.getClass().getPackageName();
    }

    /**
     * Возвращает экземпляр {@code Filter} по его имени.
     *
     * @param name имя класса {@code Filter}, объект которого нужно получить
     * @return экземпляр {@code Filter}, или {@code null}, если фильтр с таким именем не зарегистрирован
     * @see Filter#getName()
     */
    public Filter getByName(String name) {
        Filter filter = filtersMap.get(name);
        if (filter == null) {
            throw new IllegalArgumentException("Filter is not found: " + name + ". Please register this filter.");
        }
        return filter;
    }

    /**
     * Возвращает список зарегистрированных классов, реализующих интерфейс {@link Filter}.
     * Если список фильтров пуст, метод автоматически регистрирует фильтры из пакета, переданного
     * в конструктор при создании фабрики. Если пакет не был передан в конструктор, то будет
     * использован пакет, в котором находится эта фабрика. Регистрация фильтров осуществляется путем
     * сканирования файлов классов в пакете и создания экземпляров классов, реализующих
     * интерфейс {@link Filter}.
     *
     * @return список зарегистрированных фильтров
     */
    public List<Filter> getFilters() {
        if (filtersMap.isEmpty()) {
            registerFilters();
        }
        return new ArrayList<>(filtersMap.values());
    }

    /**
     * Ручная регистрация фильтров.
     * Метод позволяет зарегистрировать один или несколько фильтров вручную.
     *
     * @param filters фильтры, которые необходимо зарегистрировать
     * @throws NullPointerException если один из фильтров равен null
     */
    public void registerManually(Filter... filters) {
        for (Filter filter : filters) {
            if (filter != null) {
                this.filtersMap.put(filter.getName(), filter);
            } else {
                throw new NullPointerException("Фильтр не может быть null");
            }
        }
    }

    public void registerFilters() {
        Optional.ofNullable(this.getClass().getClassLoader().getResource(packageToScan.replace('.', '/')))
                .map(url -> new File(url.getFile()))
                .filter(file -> file.exists() && file.isDirectory())
                .ifPresent(directory -> Arrays.stream(directory.listFiles())
                        .forEach(file -> getClassOf(file)
                                .flatMap(this::createFilterInstance)
                                .ifPresent(this::registerManually)
                        ));
    }

    private Optional<Filter> createFilterInstance(Class<?> aClass) {
        try {
            if (!aClass.isInterface() && !aClass.isEnum()
                    && aClass.getDeclaredConstructor().newInstance() instanceof Filter filter) {
                return Optional.of(filter);
            }
        } catch (ReflectiveOperationException _) {   // couldn't get any declared constructor in class 'aClass'
        }
        return Optional.empty();
    }

    private Optional<Class<?>> getClassOf(File file) {
        try {
            String className = packageToScan + '.' + file.getName().replace(".class", "");
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {   // couldn't find a class in file
            return Optional.empty();
        }
    }

}
