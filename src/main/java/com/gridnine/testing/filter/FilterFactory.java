package com.gridnine.testing.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FilterFactory {

    private final List<Filter> filters = new ArrayList<>();
    private final String pack = this.getClass().getPackageName();

    public List<Filter> getFilters() {
        if (filters.isEmpty()) {
            registerFilters();
        }
        return new ArrayList<>(filters);
    }

    private void registerFilters() {
        Optional.ofNullable(this.getClass().getClassLoader().getResource(pack.replace('.', '/')))
                .map(url -> new File(url.getFile()))
                .filter(file -> file.exists() && file.isDirectory())
                .ifPresent(directory -> Arrays.stream(directory.listFiles())
                        .forEach(file -> getClassOf(file)
                                .flatMap(this::createFilterInstance)
                                .ifPresent(filters::add)
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
            String className = pack + '.' + file.getName().replace(".class", "");
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {   // couldn't find a class in file
            return Optional.empty();
        }
    }

}
