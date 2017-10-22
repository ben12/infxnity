// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.control.text;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TextInputControl;

/**
 * <p>
 * Mask to use as filter for {@link javafx.scene.control.TextFormatter TextFormatter}.
 * </p>
 * <p>
 * Example for create a {@link javafx.scene.control.TextField TextField} allowing only french phone numbers:
 * 
 * <pre>
 * <code>
 * final {@link javafx.scene.control.TextField TextField} textField = new {@link javafx.scene.control.TextField TextField}();
 * final {@link MaskCharacter}[] mask = {@link MaskBuilder}.newBuilder()
 * 	.appendLiteral("+33 ")
 * 	.appendDigit('6')
 * 	.appendLiteral(" ")
 * 	.appendDigit(2)
 * 	.appendLiteral(" ")
 * 	.appendDigit(2)
 * 	.appendLiteral(" ")
 * 	.appendDigit(2)
 * 	.appendLiteral(" ")
 * 	.appendDigit(2)
 * 	.build();
 * textField.setTextFormatter(new {@link javafx.scene.control.TextFormatter TextFormatter}<>(new {@link MaskTextFilter}(textField, false, mask)));
 * </code>
 * </pre>
 * 
 * Default text will be "+33 6 00 00 00 00".<br/>
 * Caret will be placed in 4th position : "+33 |6 00 00 00 00".<br/>
 * An navigation to the right will do that:<br/>
 * "+33 6 |00 00 00 00"<br/>
 * "+33 6 0|0 00 00 00"<br/>
 * "+33 6 00 |00 00 00"<br/>
 * "+33 6 00 0|0 00 00"<br/>
 * "+33 6 00 00 |00 00"<br/>
 * "+33 6 00 00 0|0 00"<br/>
 * "+33 6 00 00 00 |00"<br/>
 * "+33 6 00 00 00 0|0"<br/>
 * "+33 6 00 00 00 00|"<br/>
 * </p>
 * 
 * @author Benoît Moreau (ben.12)
 * @see MaskBuilder
 */
public class MaskTextFilter implements UnaryOperator<Change>
{
	private final MaskCharacter[]	mask;

	private Control					settingDefaultOn	= null;

	/**
	 * @param pMask
	 *            the mask to use
	 */
	public MaskTextFilter(@NamedArg("mask") final MaskCharacter... pMask)
	{
		mask = Arrays.copyOf(pMask, pMask.length);
	}

	/**
	 * @param input
	 *            {@link TextInputControl} where the filter will be applied
	 * @param setDefaultOnFocus
	 *            true to set the default value when it gain the focus, otherwise the default value is immediately applied
	 * @param pMask
	 *            the mask to use
	 */
	public MaskTextFilter(final TextInputControl input, final boolean setDefaultOnFocus, final MaskCharacter... pMask)
	{
		mask = Arrays.copyOf(pMask, pMask.length);

		install(input, setDefaultOnFocus);
	}

	/**
	 * @param input
	 *            {@link TextInputControl} where set the default value
	 * @param setDefaultOnFocus
	 *            true to set the default value when it gain the focus, otherwise the default value is immediately applied
	 */
	public void install(final TextInputControl input, final boolean setDefaultOnFocus)
	{
		if (input != null)
		{
			if (!setDefaultOnFocus)
			{
				applyDefault(input);
			}
			else
			{
				final ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>()
				{
					@Override
					public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
							final Boolean newValue)
					{
						if ((observable == input.focusedProperty() && newValue && !input.isPressed())
								|| (observable == input.pressedProperty() && !newValue && input.isFocused()))
						{
							applyDefault(input);
							input.focusedProperty().removeListener(this);
							input.pressedProperty().removeListener(this);
						}
					}
				};

				input.focusedProperty().addListener(focusListener);
				input.pressedProperty().addListener(focusListener);
			}
		}
	}

	/**
	 * @param input
	 *            {@link TextInputControl} where set the default value
	 */
	public void applyDefault(final TextInputControl input)
	{
		try
		{
			settingDefaultOn = input;
			final String defaultText = Stream.of(mask)
					.map(m -> Character.toString(m.getDefault()))
					.collect(Collectors.joining());
			input.setText(defaultText);

			final int firstAllowedPosition = IntStream.range(0, mask.length)
					.filter(i -> mask[i].isNavigable())
					.findFirst()
					.orElse(0);
			input.selectRange(firstAllowedPosition, firstAllowedPosition);
		}
		finally
		{
			settingDefaultOn = null;
		}
	}

	@Override
	public Change apply(final Change c)
	{
		if (settingDefaultOn == c.getControl())
		{
			return c;
		}

		if (c.isContentChange() && !correctContentChange(c))
		{
			return null;
		}

		adjustCaretPosition(c);

		return c;
	}

	private boolean correctContentChange(final Change c)
	{
		Optional<String> correctNewText = Optional.empty();

		if (c.isReplaced())
		{
			correctNewText = correctReplacedText(c);
		}
		else if (c.isAdded())
		{
			correctNewText = correctAddedText(c);
		}
		else if (c.isDeleted())
		{
			correctNewText = correctDeletedText(c);
		}

		if (correctNewText.isPresent())
		{
			final int start = c.getRangeStart();
			c.setRange(start, Math.min(start + correctNewText.get().length(), c.getControlText().length()));
			c.setText(correctNewText.get());
		}

		return correctNewText.isPresent();
	}

	private Optional<String> correctReplacedText(final Change c)
	{
		final int start = c.getRangeStart();
		final int end = c.getRangeEnd();
		final String text = c.getText();

		final StringBuilder newText = new StringBuilder(end - start);
		for (int i = start; i - start < text.length() && i < end && i < mask.length; i++)
		{
			final char ch = text.charAt(i - start);
			if (mask[i].isAllowed(ch))
			{
				newText.append(mask[i].tranform(ch));
			}
			else
			{
				return Optional.empty();
			}
		}

		for (int i = start + text.length(); i < end && i < mask.length; i++)
		{
			newText.append(mask[i].getDefault());
		}

		return Optional.of(newText.toString());
	}

	private Optional<String> correctAddedText(final Change c)
	{
		final int start = c.getRangeStart();
		final String text = c.getText();

		final StringBuilder newText = new StringBuilder(text.length());

		for (int i = start; i - start < text.length() && i < mask.length; i++)
		{
			final char ch = text.charAt(i - start);
			if (mask[i].isAllowed(ch))
			{
				newText.append(mask[i].tranform(ch));
			}
			else
			{
				return Optional.empty();
			}
		}

		return Optional.of(newText.toString());
	}

	private Optional<String> correctDeletedText(final Change c)
	{
		int start = c.getRangeStart();
		final int end = c.getRangeEnd();

		final StringBuilder newText = new StringBuilder(end - start);

		for (int i = start; i < end; i++)
		{
			newText.append(mask[i].getDefault());
		}

		// For backspace case
		for (int i = start; i > 0 && !mask[i].isNavigable(); i--, start--)
		{
			newText.insert(0, mask[i - 1].getDefault());
		}

		c.setRange(start, end);

		return Optional.of(newText.toString());
	}

	private void adjustCaretPosition(final Change c)
	{
		final int oldPosition = c.getControlCaretPosition();
		int position = Math.min(c.getCaretPosition(), mask.length);
		if (oldPosition != position)
		{
			final int sign = (position > oldPosition ? 1 : -1);
			while (position > 0 && position < mask.length && !mask[position].isNavigable())
			{
				position += sign;
			}
			while (position < mask.length && !mask[position].isNavigable())
			{
				position++;
			}
		}

		position = Math.min(position, c.getControlNewText().length());

		if (c.getAnchor() == c.getCaretPosition())
		{
			c.setAnchor(position);
		}
		c.setCaretPosition(position);
	}

	/**
	 * Mask character interface.
	 * 
	 * @author Benoît Moreau (ben.12)
	 */
	public interface MaskCharacter
	{
		/**
		 * @param c
		 *            an input character
		 * @return true if the character is allowed, false otherwise
		 */
		boolean isAllowed(char c);

		/**
		 * @param c
		 *            an input character
		 * @return the transformed character to set
		 */
		char tranform(char c);

		/**
		 * @return the default character
		 */
		char getDefault();

		/**
		 * @return true if caret can be placed before the character, false otherwise
		 */
		boolean isNavigable();
	}
}
