package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Flight;

import java.util.List;

public interface Filter {

    /**
     * Основной метод для фильтрации элементов типа {@code Flight}. Поступившая на вход коллекция будет
     * подвергнута фильтрации при помощи одного предиката, определенного ссылкой на метод {@link Filter#test(Flight)}.
     * После этого результирующий поток будет собран в список и возвращен. Этот метод будет отфильтровывать элементы,
     * исключая их из результирующего потока в том случае, если метод {@link Filter#test(Flight)} вернул {@code false}
     *
     * @param flights исходный список объектов {@link Flight}, который необходимо отфильтровать
     * @return Список объектов {@link Flight}, отфильтрованный на основании предиката,
     * ссылающегося на метод {@link Filter#test(Flight)}
     */
    default List<Flight> doFilter(List<Flight> flights) {
        return flights.stream().filter(this::test).toList();
    }

    /**
     * Метод, использующийся для фильтрации элементов в {@link Filter#doFilter(List)}.
     * Реализация этого метода должна возвращать {@code true} в том случае, если
     * объект  необходимо оставить и передать в результирующий поток
     *
     * @param flight объект, для которого требуется принять решение на
     *               основании условия, заданного конкретной реализацией
     * @return {@code true} если переданный аргумент {@code flight} прошел
     * фильтрацию и должен быть включен в результирующий поток.<br>
     * {@code false} в противном случае
     */
    boolean test(Flight flight);

    /**
     * Метод возвращает имя фильтра.
     * Если не переопределено, то возвращает имя класса фильтра (без пакета), на котором вызывается
     * @return имя фильтра
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
