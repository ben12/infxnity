// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.control.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.ben12.infxnity.control.text.MaskTextFilter.MaskCharacter;

import javafx.util.Builder;

/**
 * Mask builder use to create a mask for {@link MaskTextFilter}.
 * 
 * @author Benoît Moreau (ben.12)
 * @see MaskTextFilter
 */
public class MaskBuilder implements Builder<MaskCharacter[]>
{
	private final List<MaskCharacter> mask = new ArrayList<>();

	public static MaskBuilder newBuilder()
	{
		return new MaskBuilder();
	}

	/**
	 * Appends an unmodifiable text.
	 * 
	 * @param value
	 *            the literal string to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLiteral(final String value)
	{
		for (final char c : value.toCharArray())
		{
			mask.add(new LiteralMaskCharacter(c));
		}
		return this;
	}

	/**
	 * Appends a character where only digit are allowed.
	 * Default value is '0'.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendDigit()
	{
		return appendDigit(1);
	}

	/**
	 * Appends a character where only digit are allowed.
	 * 
	 * @param defaultValue
	 *            the default value
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendDigit(final char defaultValue)
	{
		return appendDigit(1, defaultValue);
	}

	/**
	 * Appends some characters where only digit are allowed.
	 * Default value is '0'.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendDigit(final int repeat)
	{
		return appendDigit(repeat, '0');
	}

	/**
	 * Appends some characters where only digit are allowed.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendDigit(final int repeat, final char defaultValue)
	{
		return append(repeat, Character::isDigit, defaultValue);
	}

	/**
	 * Appends a character where only upper case letter are allowed.
	 * Typing lower case character will set an upper case character.
	 * Default value is 'A'.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendUpperCase()
	{
		return appendUpperCase(1);
	}

	/**
	 * Appends a character where only upper case letter are allowed.
	 * Typing lower case character will set an upper case character.
	 * 
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendUpperCase(final char defaultValue)
	{
		return appendUpperCase(1, defaultValue);
	}

	/**
	 * Appends some characters where only upper case letter are allowed.
	 * Typing lower case character will set an upper case character.
	 * Default value is 'A'.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendUpperCase(final int repeat)
	{
		return appendUpperCase(repeat, 'A');
	}

	/**
	 * Appends some characters where only upper case letter are allowed.
	 * Typing lower case character will set an upper case character.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendUpperCase(final int repeat, final char defaultValue)
	{
		return append(repeat, Character::isLetter, Character::toUpperCase, defaultValue);
	}

	/**
	 * Appends a character where only lower case letter are allowed.
	 * Typing upper case character will set a lower case character.
	 * Default value is 'a'.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLowerCase()
	{
		return appendLowerCase(1);
	}

	/**
	 * Appends a character where only lower case letter are allowed.
	 * Typing upper case character will set a lower case character.
	 * 
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLowerCase(final char defaultValue)
	{
		return appendLowerCase(1, defaultValue);
	}

	/**
	 * Appends some characters where only lower case letter are allowed.
	 * Typing upper case character will set a lower case character.
	 * Default value is 'a'.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLowerCase(final int repeat)
	{
		return appendLowerCase(repeat, 'a');
	}

	/**
	 * Appends some characters where only lower case letter are allowed.
	 * Typing upper case character will set a lower case character.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLowerCase(final int repeat, final char defaultValue)
	{
		return append(repeat, Character::isLetter, Character::toLowerCase, defaultValue);
	}

	/**
	 * Appends a character where only letter are allowed.
	 * Default value is 'a'.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetter()
	{
		return appendLetter(1);
	}

	/**
	 * Appends a character where only letter are allowed.
	 * 
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetter(final char defaultValue)
	{
		return appendLetter(1, defaultValue);
	}

	/**
	 * Appends some characters where only letter are allowed.
	 * Default value is 'a'.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetter(final int repeat)
	{
		return appendLetter(repeat, 'a');
	}

	/**
	 * Appends some characters where only letter are allowed.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetter(final int repeat, final char defaultValue)
	{
		return append(repeat, Character::isLetter, defaultValue);
	}

	/**
	 * Appends a character where only letter or digit are allowed.
	 * Default value is 'a'.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetterOrDigit()
	{
		return appendLetterOrDigit(1);
	}

	/**
	 * Appends a character where only letter or digit are allowed.
	 * 
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetterOrDigit(final char defaultValue)
	{
		return appendLetterOrDigit(1, defaultValue);
	}

	/**
	 * Appends some characters where only letter or digit are allowed.
	 * Default value is 'a'.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetterOrDigit(final int repeat)
	{
		return appendLetterOrDigit(repeat, 'a');
	}

	/**
	 * Appends some characters where only letter or digit are allowed.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendLetterOrDigit(final int repeat, final char defaultValue)
	{
		return append(repeat, Character::isLetterOrDigit, defaultValue);
	}

	/**
	 * Appends a character where only hexadecimal are allowed.
	 * Typing lower case hexadecimal letter will set an upper case hexadecimal letter.
	 * Default value is 'a'.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendHexa()
	{
		return appendHexa(1);
	}

	/**
	 * Appends a character where only hexadecimal are allowed.
	 * Typing lower case hexadecimal letter will set an upper case hexadecimal letter.
	 * 
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendHexa(final char defaultValue)
	{
		return appendHexa(1, defaultValue);
	}

	/**
	 * Appends some characters where only hexadecimal are allowed.
	 * Typing lower case hexadecimal letter will set an upper case hexadecimal letter.
	 * Default value is 'a'.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendHexa(final int repeat)
	{
		return appendHexa(repeat, '0');
	}

	/**
	 * Appends some characters where only hexadecimal are allowed.
	 * Typing lower case hexadecimal letter will set an upper case hexadecimal letter.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendHexa(final int repeat, final char defaultValue)
	{
		return append(repeat,
				c -> (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7'
						|| c == '8' || c == '9' || c == 'a' || c == 'A' || c == 'b' || c == 'B' || c == 'c' || c == 'C'
						|| c == 'd' || c == 'D' || c == 'e' || c == 'E' || c == 'f' || c == 'F'),
				Character::toUpperCase, defaultValue);
	}

	/**
	 * Appends a character where any character are allowed.
	 * Default value is ' '.
	 * 
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendAny()
	{
		return appendAny(1);
	}

	/**
	 * Appends a character where any character are allowed.
	 * 
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendAny(final char defaultValue)
	{
		return appendAny(1, defaultValue);
	}

	/**
	 * Appends some characters where any character are allowed.
	 * Default value is ' '.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendAny(final int repeat)
	{
		return appendAny(repeat, ' ');
	}

	/**
	 * Appends some characters where any character are allowed.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder appendAny(final int repeat, final char defaultValue)
	{
		return append(repeat, c -> true, defaultValue);
	}

	/**
	 * Appends some characters where allowing only character defined by the <code>allowed</code> predicate.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param allowed
	 *            {@link Predicate} called to allow characters
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder append(final int repeat, final Predicate<Character> allowed, final char defaultValue)
	{
		return append(repeat, new DefaultMaskCharacter(allowed, defaultValue));
	}

	/**
	 * Appends some characters where allowing only character defined by the <code>allowed</code> predicate.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param allowed
	 *            {@link Predicate} called to allow characters
	 * @param transformation
	 *            transformation to apply to the input characters
	 * @param defaultValue
	 *            the default value for each character
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder append(final int repeat, final Predicate<Character> allowed,
			final UnaryOperator<Character> transformation, final char defaultValue)
	{
		return append(repeat, new DefaultMaskCharacter(allowed, transformation, defaultValue));
	}

	/**
	 * Appends a {@link MaskCharacter}.
	 * 
	 * @param maskCharacter
	 *            the {@link MaskCharacter} to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder append(final MaskCharacter maskCharacter)
	{
		return append(1, maskCharacter);
	}

	/**
	 * Appends some {@link MaskCharacter}.
	 * 
	 * @param repeat
	 *            number of character to append
	 * @param maskCharacter
	 *            the {@link MaskCharacter} to append
	 * @return this {@link MaskBuilder}
	 */
	public MaskBuilder append(final int repeat, final MaskCharacter maskCharacter)
	{
		for (int i = 0; i < repeat; i++)
		{
			mask.add(maskCharacter);
		}
		return this;
	}

	@Override
	public MaskCharacter[] build()
	{
		return mask.toArray(new MaskCharacter[mask.size()]);
	}

	private static final class DefaultMaskCharacter implements MaskCharacter
	{
		private final char						defaultValue;

		private final Predicate<Character>		allowed;

		private final UnaryOperator<Character>	transformation;

		public DefaultMaskCharacter(final Predicate<Character> pAllowed, final char pDefaultValue)
		{
			this(pAllowed, UnaryOperator.identity(), pDefaultValue);
		}

		public DefaultMaskCharacter(final Predicate<Character> pAllowed, final UnaryOperator<Character> pTransformation,
				final char pDefaultValue)
		{
			allowed = pAllowed;
			defaultValue = pDefaultValue;
			transformation = pTransformation;
		}

		@Override
		public boolean isAllowed(final char c)
		{
			return allowed.test(c);
		}

		@Override
		public char tranform(final char c)
		{
			return transformation.apply(c);
		}

		@Override
		public char getDefault()
		{
			return defaultValue;
		}

		@Override
		public boolean isNavigable()
		{
			return true;
		}
	}

	private static final class LiteralMaskCharacter implements MaskCharacter
	{
		private final char constant;

		public LiteralMaskCharacter(final char pConstant)
		{
			constant = pConstant;
		}

		@Override
		public boolean isAllowed(final char c)
		{
			return constant == c;
		}

		@Override
		public char tranform(final char c)
		{
			return constant;
		}

		@Override
		public char getDefault()
		{
			return constant;
		}

		@Override
		public boolean isNavigable()
		{
			return false;
		}
	}
}
