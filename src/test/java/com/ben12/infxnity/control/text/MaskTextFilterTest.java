// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.control.text;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.ben12.infxnity.control.text.MaskTextFilter.MaskCharacter;
import com.google.common.collect.ImmutableMap;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * @author Benoît Moreau (ben.12)
 */
public class MaskTextFilterTest extends ApplicationTest
{
	// QWERTY and AZERTY compatible
	private static final Map<KeyCode, Character>	SOME_ANY		= ImmutableMap.<KeyCode, Character> builder()
			.put(KeyCode.COMMA, ',')
			.put(KeyCode.ADD, '+')
			.put(KeyCode.SPACE, ' ')
			.put(KeyCode.EQUALS, '=')
			.put(KeyCode.Z, 'z')
			.put(KeyCode.NUMPAD3, '3')
			.build();

	private static final Map<KeyCode, Character>	NUMBERS			= ImmutableMap.<KeyCode, Character> builder()
			.put(KeyCode.NUMPAD9, '9')
			.put(KeyCode.NUMPAD8, '8')
			.put(KeyCode.NUMPAD7, '7')
			.put(KeyCode.NUMPAD6, '6')
			.put(KeyCode.NUMPAD5, '5')
			.put(KeyCode.NUMPAD4, '4')
			.put(KeyCode.NUMPAD3, '3')
			.put(KeyCode.NUMPAD2, '2')
			.put(KeyCode.NUMPAD1, '1')
			.put(KeyCode.NUMPAD0, '0')
			.build();

	private static final Map<KeyCode, Character>	HEXA			= ImmutableMap.<KeyCode, Character> builder()
			.put(KeyCode.NUMPAD9, '9')
			.put(KeyCode.NUMPAD8, '8')
			.put(KeyCode.NUMPAD7, '7')
			.put(KeyCode.NUMPAD6, '6')
			.put(KeyCode.NUMPAD5, '5')
			.put(KeyCode.NUMPAD4, '4')
			.put(KeyCode.NUMPAD3, '3')
			.put(KeyCode.NUMPAD2, '2')
			.put(KeyCode.NUMPAD1, '1')
			.put(KeyCode.A, 'A')
			.put(KeyCode.B, 'B')
			.put(KeyCode.C, 'C')
			.put(KeyCode.D, 'D')
			.put(KeyCode.E, 'E')
			.put(KeyCode.F, 'F')
			.put(KeyCode.NUMPAD0, '0')
			.build();

	private static final Map<KeyCode, Character>	LOWER_LETTER	= IntStream.range(0, 26)
			.mapToObj(Integer::valueOf)
			.collect(Collectors.toMap(i -> KeyCode.getKeyCode(Character.toString((char) ('A' + i))),
					i -> (char) ('a' + i), (t, u) -> t, LinkedHashMap::new));

	private static final Map<KeyCode, Character>	UPPER_LETTER	= IntStream.range(0, 26)
			.mapToObj(Integer::valueOf)
			.collect(Collectors.toMap(i -> KeyCode.getKeyCode(Character.toString((char) ('A' + i))),
					i -> (char) ('A' + i), (t, u) -> t, LinkedHashMap::new));

	private static final String						DEFAULT_TEXT	= "\\0,0.a$a!aAU ^_/";

	@Override
	public void start(final Stage stage) throws Exception
	{
		final TextField textField = new TextField();
		textField.setId("formatted");

		final MaskCharacter[] mask = MaskBuilder.newBuilder()
				.appendLiteral("\\")
				.appendDigit()
				.appendLiteral(",")
				.appendHexa()
				.appendLiteral(".")
				.appendLetter()
				.appendLiteral("$")
				.appendLetterOrDigit()
				.appendLiteral("!")
				.appendLowerCase()
				.appendUpperCase()
				.appendLiteral("U")
				.appendAny()
				.appendLiteral("^")
				.append(1, c -> (c == '-' || c == '+' || c == 'M' || c == 'P'), c -> (c == '+' || c == 'P') ? 'P' : 'M',
						'_')
				.appendLiteral("/")
				.build();

		textField.setTextFormatter(new TextFormatter<>(new MaskTextFilter(textField, true, mask)));

		final Scene scene = new Scene(textField, 200, 50);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void init() throws Exception
	{
		release(KeyCode.SHIFT);
		super.init();
	}

	@Test
	public void initalizationTest()
	{
		verifyThat("#formatted", hasText(anyOf(nullValue(String.class), equalTo(""))));
	}

	@Test
	public void onFocusedTest()
	{
		clickOn("#formatted");
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
	}

	@Test
	public void moveCaretTest()
	{
		clickOn("#formatted");
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 1);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 3);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 5);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 7);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 10);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 12);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 14);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 16);
		type(KeyCode.RIGHT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 16);

		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 14);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 12);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 10);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 7);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 5);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 3);
		type(KeyCode.LEFT);
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 1);
	}

	@Test
	public void digitTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(1, 1));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 1);
		type(KeyCode.A);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 1);

		for (final Entry<KeyCode, Character> e : NUMBERS.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\" + e.getValue() + ",0.a$a!aAU ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 3);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(1, 1));
		}
	}

	@Test
	public void hexaTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(3, 3));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 3);
		type(KeyCode.G);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 3);

		for (final Entry<KeyCode, Character> e : HEXA.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0," + e.getValue() + ".a$a!aAU ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 5);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(3, 3));
		}
	}

	@Test
	public void letterTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(5, 5));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 5);
		type(KeyCode.NUMPAD0);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 5);

		for (final Entry<KeyCode, Character> e : LOWER_LETTER.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0,0." + e.getValue() + "$a!aAU ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 7);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(5, 5));
		}

		try
		{
			press(KeyCode.SHIFT);
			for (final Entry<KeyCode, Character> e : UPPER_LETTER.entrySet())
			{
				type(e.getKey());
				verifyThat("#formatted", hasText("\\0,0." + e.getValue() + "$a!aAU ^_/"));
				verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 7);
				interact(() -> ((TextField) lookup("#formatted").query()).selectRange(5, 5));
			}
		}
		finally
		{
			release(KeyCode.SHIFT);
		}
	}

	@Test
	public void letterOrDigitTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(7, 7));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 7);
		type(KeyCode.ADD);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 7);

		for (final Entry<KeyCode, Character> e : LOWER_LETTER.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0,0.a$" + e.getValue() + "!aAU ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(7, 7));
		}

		try
		{
			press(KeyCode.SHIFT);
			for (final Entry<KeyCode, Character> e : UPPER_LETTER.entrySet())
			{
				type(e.getKey());
				verifyThat("#formatted", hasText("\\0,0.a$" + e.getValue() + "!aAU ^_/"));
				verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);
				interact(() -> ((TextField) lookup("#formatted").query()).selectRange(7, 7));
			}
		}
		finally
		{
			release(KeyCode.SHIFT);
		}

		for (final Entry<KeyCode, Character> e : NUMBERS.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0,0.a$" + e.getValue() + "!aAU ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(7, 7));
		}
	}

	@Test
	public void lowerCaseTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(9, 9));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);
		type(KeyCode.MULTIPLY);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 9);

		for (final Entry<KeyCode, Character> e : LOWER_LETTER.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0,0.a$a!" + e.getValue() + "AU ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 10);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(9, 9));
		}

		try
		{
			press(KeyCode.SHIFT);
			for (final Entry<KeyCode, Character> e : UPPER_LETTER.entrySet())
			{
				type(e.getKey());
				verifyThat("#formatted", hasText("\\0,0.a$a!" + Character.toLowerCase(e.getValue()) + "AU ^_/"));
				verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 10);
				interact(() -> ((TextField) lookup("#formatted").query()).selectRange(9, 9));
			}
		}
		finally
		{
			release(KeyCode.SHIFT);
		}
	}

	@Test
	public void upperCaseTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(10, 10));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 10);
		type(KeyCode.COMMA);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 10);

		for (final Entry<KeyCode, Character> e : LOWER_LETTER.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0,0.a$a!a" + Character.toUpperCase(e.getValue()) + "U ^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 12);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(10, 10));
		}

		try
		{
			press(KeyCode.SHIFT);
			for (final Entry<KeyCode, Character> e : UPPER_LETTER.entrySet())
			{
				type(e.getKey());
				verifyThat("#formatted", hasText("\\0,0.a$a!a" + e.getValue() + "U ^_/"));
				verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 12);
				interact(() -> ((TextField) lookup("#formatted").query()).selectRange(10, 10));
			}
		}
		finally
		{
			release(KeyCode.SHIFT);
		}
	}

	@Test
	public void anyTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(12, 12));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 12);

		for (final Entry<KeyCode, Character> e : SOME_ANY.entrySet())
		{
			type(e.getKey());
			verifyThat("#formatted", hasText("\\0,0.a$a!aAU" + e.getValue() + "^_/"));
			verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 14);
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(12, 12));
		}
	}

	@Test
	public void customTest()
	{
		clickOn("#formatted");

		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(14, 14));

		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 14);
		type(KeyCode.NUMPAD0);
		verifyThat("#formatted", hasText(DEFAULT_TEXT));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 14);

		type(KeyCode.ADD);
		verifyThat("#formatted", hasText("\\0,0.a$a!aAU ^P/"));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 16);
		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(14, 14));

		type(KeyCode.SUBTRACT);
		verifyThat("#formatted", hasText("\\0,0.a$a!aAU ^M/"));
		verifyThat("#formatted", (final TextField textField) -> textField.getCaretPosition() == 16);
		interact(() -> ((TextField) lookup("#formatted").query()).selectRange(14, 14));
	}

	@Test
	public void pasteTest() throws Exception
	{
		final String invalidText = "\\4,#.f$1!fTU*^M/";
		final String validText = "\\4,B.f$1!fTU*^M/";

		clickOn("#formatted");

		{
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(0, 16));

			interact(() -> {
				final ClipboardContent content = new ClipboardContent();
				content.putString(invalidText);
				Clipboard.getSystemClipboard().setContent(content);
			});

			push(KeyCode.CONTROL, KeyCode.V);

			verifyThat("#formatted", hasText(DEFAULT_TEXT));
		}

		{
			interact(() -> ((TextField) lookup("#formatted").query()).selectRange(0, 16));

			interact(() -> {
				final ClipboardContent content = new ClipboardContent();
				content.putString(validText);
				Clipboard.getSystemClipboard().setContent(content);
			});

			push(KeyCode.CONTROL, KeyCode.V);

			verifyThat("#formatted", hasText(validText));
		}
	}
}
