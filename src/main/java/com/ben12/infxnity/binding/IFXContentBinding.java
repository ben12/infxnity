// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.binding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Provide lists content binding with type mapping.
 * 
 * @author Benoît Moreau (ben.12)
 */
public class IFXContentBinding
{
    private IFXContentBinding()
    {
    }

    /**
     * Bind the content of <code>list2</code> in <code>list1</code> using <code>mapper</code> to convert each element.
     * 
     * @param list1
     *            destination list
     * @param list2
     *            source observable list
     * @param mapper
     *            list elements mapper from type E to the type R
     * @param <E>
     *            type of elements to bind in the source list
     * @param <R>
     *            type of elements binded in the destination list
     */
    public static <E, R> void bind(final List<R> list1, final ObservableList<? extends E> list2,
            final Function<E, R> mapper)
    {
        final IFXListContentBinding<E, R> binding = new IFXListContentBinding<>(list1, mapper);

        final List<R> convertedList = list2.stream()
                                           .map(mapper)
                                           .collect(Collectors.toCollection(() -> new ArrayList<>(list2.size())));
        if (list1 instanceof ObservableList)
        {
            ((ObservableList<R>) list1).setAll(convertedList);
        }
        else
        {
            list1.clear();
            list1.addAll(convertedList);
        }

        list2.removeListener(binding);
        list2.addListener(binding);
    }

    /**
     * Un-bind <code>list1</code> of <code>list2</code>.
     * 
     * @param list1
     *            destination list
     * @param list2
     *            source observable list
     */
    public static <E, R> void unbind(final List<R> list1, final ObservableList<? extends E> list2)
    {
        list2.removeListener(new IFXListContentBinding<>(list1, null));
    }

    /**
     * Listen and synchronize a list with another using a mapper.
     * 
     * @author Benoît Moreau (ben.12)
     * @param <E>
     *            type of elements to bind in the source list
     * @param <R>
     *            type of elements binded in the destination list
     */
    private static class IFXListContentBinding<E, R> implements ListChangeListener<E>, WeakListener
    {
        private final WeakReference<List<R>> destRef;

        private final Function<E, R>         mapper;

        public IFXListContentBinding(final List<R> destination, final Function<E, R> pMapper)
        {
            destRef = new WeakReference<>(destination);
            mapper = pMapper;
        }

        @Override
        public void onChanged(final ListChangeListener.Change<? extends E> c)
        {
            final List<R> list = destRef.get();
            if (list == null)
            {
                c.getList().removeListener(this);
            }
            else
            {
                while (c.next())
                {
                    if (c.wasPermutated())
                    {
                        list.subList(c.getFrom(), c.getTo()).clear();
                        list.addAll(c.getFrom(),
                                    c.getList()
                                     .subList(c.getFrom(), c.getTo())
                                     .stream()
                                     .map(mapper)
                                     .collect(Collectors.toCollection(() -> new ArrayList<>(c.getTo() - c.getFrom()))));
                    }
                    else
                    {
                        if (c.wasUpdated() || c.wasReplaced())
                        {
                            final Iterator<? extends E> it = c.getList().subList(c.getFrom(), c.getTo()).iterator();
                            list.subList(c.getFrom(), c.getTo()).replaceAll(r -> mapper.apply(it.next()));
                        }
                        else if (c.wasRemoved())
                        {
                            list.subList(c.getFrom(), c.getFrom() + c.getRemovedSize()).clear();
                        }
                        else if (c.wasAdded())
                        {
                            list.addAll(c.getFrom(),
                                        c.getAddedSubList()
                                         .stream()
                                         .map(mapper)
                                         .collect(Collectors.toCollection(() -> new ArrayList<>(c.getAddedSize()))));
                        }
                    }
                }
            }
        }

        @Override
        public boolean wasGarbageCollected()
        {
            return destRef.get() == null;
        }

        @Override
        public int hashCode()
        {
            final List<R> list = destRef.get();
            return (list == null) ? 0 : list.hashCode();
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            final List<R> list1 = destRef.get();
            if (list1 == null)
            {
                return false;
            }

            if (obj instanceof IFXListContentBinding<?, ?>)
            {
                final IFXListContentBinding<?, ?> other = (IFXListContentBinding<?, ?>) obj;
                final List<?> list2 = other.destRef.get();
                return list1 == list2;
            }
            return false;
        }
    }
}
