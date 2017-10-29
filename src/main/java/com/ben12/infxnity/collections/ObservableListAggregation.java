// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.collections;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

/**
 * <p>
 * An {@link ObservableList} aggregating other {@link ObservableList}(s).
 * </p>
 * <p>
 * All events of aggregated {@link ObservableList}(s) are forwarded.
 * </p>
 * <p>
 * The list of aggregated {@link ObservableList}(s) can be modified to add, remove or replace one or more of the aggregated {@link ObservableList}(s).
 * An appropriate event will be fired.
 * </p>
 * <p>
 * An instance of {@link ObservableListAggregation} is a read only list. Any attempt to modify the list will throw an {@link UnsupportedOperationException}.
 * </p>
 * 
 * @author Benoît Moreau (ben.12)
 */
public class ObservableListAggregation<E> extends ObservableListBase<E>
{
    private final ObservableList<ObservableList<? extends E>> lists = FXCollections.observableArrayList();

    private ListChangeListener<E>                             listener;

    /**
     * Construct an empty list.
     */
    public ObservableListAggregation()
    {
        this(Collections.emptyList());
    }

    /**
     * Construct an aggregate list.
     * 
     * @param pLists
     *            list of {@link ObservableList}s to aggregate
     */
    @SuppressWarnings("unchecked")
    public ObservableListAggregation(final List<ObservableList<? extends E>> pLists)
    {
        this(pLists.toArray(new ObservableList[pLists.size()]));
    }

    /**
     * Construct an aggregate list.
     * 
     * @param pLists
     *            {@link ObservableList}s to aggregate
     */
    @SafeVarargs
    public ObservableListAggregation(final ObservableList<? extends E>... pLists)
    {
        lists.setAll(pLists);

        listener = c -> {
            final ObservableList<? extends E> source = c.getList();
            final int listIndex = lists.indexOf(source);

            beginChange();
            while (c.next())
            {
                final int offset = lists.stream().limit(listIndex).mapToInt(List::size).sum();
                final int from = offset + c.getFrom();
                final int to = offset + c.getTo();

                if (c.wasPermutated())
                {
                    final int[] perm = new int[to - from];
                    for (int i = 0; i < perm.length; i++)
                    {
                        perm[i] = offset + c.getPermutation(i);
                    }
                    nextPermutation(from, to, perm);
                }
                else
                {
                    if (c.wasUpdated())
                    {
                        for (int i = from; i < to; i++)
                        {
                            nextUpdate(i);
                        }
                    }
                    if (c.wasRemoved())
                    {
                        nextRemove(from, c.getRemoved());
                    }
                    if (c.wasAdded())
                    {
                        nextAdd(from, to);
                    }
                }
            }
            endChange();
        };
        lists.forEach(l -> l.addListener(listener));

        lists.addListener((ListChangeListener<ObservableList<? extends E>>) c -> {
            beginChange();
            while (c.next())
            {
                final int from = c.getList().stream().limit(c.getFrom()).mapToInt(List::size).sum();

                if (c.wasRemoved())
                {
                    c.getRemoved().forEach(l -> l.removeListener(listener));
                    nextRemove(from, c.getRemoved().stream().flatMap(List::stream).collect(Collectors.toList()));
                }
                if (c.wasAdded())
                {
                    c.getAddedSubList().forEach(l -> l.addListener(listener));
                    nextAdd(from, from + c.getAddedSubList().stream().mapToInt(List::size).sum());
                }
            }
            endChange();
        });
    }

    /**
     * <p>
     * Returns the {@link ObservableList} of aggregated {@link ObservableList}s.
     * </p>
     * <p>
     * The returned {@link ObservableList} can be modified.
     * </p>
     * 
     * @return the {@link ObservableList} of aggregated {@link ObservableList}s.
     */
    public ObservableList<ObservableList<? extends E>> getLists()
    {
        return lists;
    }

    @Override
    public E get(final int index)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException();
        }

        return lists.stream() //
                    .flatMap(List::stream)
                    .skip(index)
                    .findFirst()
                    .orElseThrow(IndexOutOfBoundsException::new);
    }

    @Override
    public int size()
    {
        return lists.stream() //
                    .mapToInt(List::size)
                    .sum();
    }
}
