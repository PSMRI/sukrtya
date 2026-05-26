package com.sukrtya.siwan.beneficiary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sukrtya.siwan.forms.AnswerType;
import com.sukrtya.siwan.forms.FormQuestion;
import com.sukrtya.siwan.forms.OptionSet;
import com.sukrtya.siwan.forms.OptionSetRepository;
import com.sukrtya.siwan.forms.OptionValue;
import com.sukrtya.siwan.forms.OptionValueRepository;

/**
 * Validates an answers JSON payload against the configured questions of a form. Verifies
 * mandatory presence, numeric/date min-max bounds, single/multi-choice option membership, and
 * basic type shape. Returns a list of human-readable violations; an empty list means OK.
 *
 * <p>Computed (read-only) fields are intentionally skipped — clients should not submit values for
 * them; if they do, the value is allowed but recomputed server-side on read.
 */
@Component
public class FormResponseValidator {

	private static final DateTimeFormatter DATE_DDMMYYYY_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DATE_DDMMYYYY_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	private final OptionSetRepository optionSetRepository;
	private final OptionValueRepository optionValueRepository;

	public FormResponseValidator(OptionSetRepository optionSetRepository, OptionValueRepository optionValueRepository) {
		this.optionSetRepository = optionSetRepository;
		this.optionValueRepository = optionValueRepository;
	}

	public List<String> validate(List<FormQuestion> questions, Map<String, Object> answers) {
		List<String> errors = new ArrayList<>();
		for (FormQuestion q : questions) {
			if (q.getComputedKind() != null) {
				continue;
			}
			Object value = answers != null ? answers.get(q.getCode()) : null;
			boolean missing = isMissing(value);
			if (q.isMandatory() && missing) {
				errors.add(q.getCode() + " (" + q.getLabelEn() + "): is required.");
				continue;
			}
			if (missing) {
				continue;
			}
			dispatchByType(q, value, errors);
		}
		return errors;
	}

	/**
	 * Explicit if-chain over the {@link AnswerType} enum (instead of a switch) so the compiler does
	 * not emit a synthetic {@code FormResponseValidator$1} switch-table class. That synthetic class
	 * has bitten us before when stale build artefacts left the JVM unable to load it; the if-chain
	 * keeps the dispatch fully inside this source file.
	 */
	private void dispatchByType(FormQuestion q, Object value, List<String> errors) {
		AnswerType t = q.getAnswerType();
		if (t == null) {
			errors.add(q.getCode() + " (" + q.getLabelEn() + "): question has no configured answerType.");
			return;
		}
		if (t == AnswerType.TEXT) {
			validateText(q, value, errors);
		}
		else if (t == AnswerType.NUMERIC) {
			validateNumeric(q, value, errors);
		}
		else if (t == AnswerType.DATE) {
			validateDate(q, value, errors);
		}
		else if (t == AnswerType.SINGLE_CHOICE) {
			validateSingleChoice(q, value, errors);
		}
		else if (t == AnswerType.MULTI_CHOICE) {
			validateMultiChoice(q, value, errors);
		}
	}

	private static boolean isMissing(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String s) {
			return s.isBlank();
		}
		if (value instanceof List<?> l) {
			return l.isEmpty();
		}
		return false;
	}

	private static void validateText(FormQuestion q, Object value, List<String> errors) {
		String s = String.valueOf(value);
		if (q.getMaxValue() != null) {
			try {
				int max = Integer.parseInt(q.getMaxValue());
				if (s.length() > max) {
					errors.add(q.getCode() + ": text length " + s.length() + " exceeds max " + max + ".");
				}
			}
			catch (NumberFormatException ignored) {
				// max_value not numeric for TEXT -> ignore silently
			}
		}
	}

	private static void validateNumeric(FormQuestion q, Object value, List<String> errors) {
		BigDecimal n;
		try {
			n = new BigDecimal(String.valueOf(value));
		}
		catch (NumberFormatException ex) {
			errors.add(q.getCode() + " (" + q.getLabelEn() + "): not a number ('" + value + "').");
			return;
		}
		if (q.getMinValue() != null) {
			try {
				BigDecimal min = new BigDecimal(q.getMinValue());
				if (n.compareTo(min) < 0) {
					errors.add(q.getCode() + ": value " + n + " is below min " + min + ".");
				}
			}
			catch (NumberFormatException ignored) {
				// ignore non-numeric min
			}
		}
		if (q.getMaxValue() != null) {
			try {
				BigDecimal max = new BigDecimal(q.getMaxValue());
				if (n.compareTo(max) > 0) {
					errors.add(q.getCode() + ": value " + n + " exceeds max " + max + ".");
				}
			}
			catch (NumberFormatException ignored) {
				// ignore non-numeric max
			}
		}
	}

	private static void validateDate(FormQuestion q, Object value, List<String> errors) {
		LocalDate d = parseDate(value);
		if (d == null) {
			errors.add(q.getCode() + " (" + q.getLabelEn() + "): not a recognised date ('" + value + "').");
			return;
		}
		LocalDate min = parseDateLoose(q.getMinValue());
		LocalDate max = parseDateLoose(q.getMaxValue());
		if (min != null && d.isBefore(min)) {
			errors.add(q.getCode() + ": date " + d + " is before min " + min + ".");
		}
		if (max != null && d.isAfter(max)) {
			errors.add(q.getCode() + ": date " + d + " is after max " + max + ".");
		}
	}

	private void validateSingleChoice(FormQuestion q, Object value, List<String> errors) {
		Set<String> allowed = allowedValues(q.getOptionSetCode());
		String v = String.valueOf(value);
		if (allowed != null && !allowed.isEmpty() && !allowed.contains(v)) {
			errors.add(q.getCode() + " (" + q.getLabelEn() + "): '" + v + "' is not a valid option (allowed: " + allowed + ").");
		}
	}

	private void validateMultiChoice(FormQuestion q, Object value, List<String> errors) {
		Set<String> allowed = allowedValues(q.getOptionSetCode());
		if (allowed == null || allowed.isEmpty()) {
			return;
		}
		List<?> chosen;
		if (value instanceof List<?> l) {
			chosen = l;
		}
		else {
			// Tolerate comma/slash strings from clients that don't quite serialise arrays yet.
			chosen = List.of(String.valueOf(value).split("[/,;]"));
		}
		for (Object item : chosen) {
			String v = String.valueOf(item).trim();
			if (!v.isEmpty() && !allowed.contains(v)) {
				errors.add(q.getCode() + ": '" + v + "' is not a valid option (allowed: " + allowed + ").");
			}
		}
	}

	private Set<String> allowedValues(String optionSetCode) {
		if (optionSetCode == null) {
			return null;
		}
		OptionSet set = optionSetRepository.findByCodeIgnoreCase(optionSetCode).orElse(null);
		if (set == null) {
			return Set.of();
		}
		Set<String> out = new HashSet<>();
		for (OptionValue ov : optionValueRepository.findByOptionSetIdOrderBySequenceAscValueAsc(set.getId())) {
			out.add(ov.getValue());
		}
		return out;
	}

	private static LocalDate parseDate(Object value) {
		if (value instanceof LocalDate d) {
			return d;
		}
		return parseDateLoose(String.valueOf(value));
	}

	/** Accepts ISO ({@code yyyy-MM-dd}) and the dd/MM/yyyy + dd-MM-yyyy forms commonly used in the Excel. */
	private static LocalDate parseDateLoose(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String s = raw.trim();
		try {
			return LocalDate.parse(s);
		}
		catch (DateTimeParseException ignored) {
			// fall through
		}
		try {
			return LocalDate.parse(s, DATE_DDMMYYYY_SLASH);
		}
		catch (DateTimeParseException ignored) {
			// fall through
		}
		try {
			return LocalDate.parse(s, DATE_DDMMYYYY_DASH);
		}
		catch (DateTimeParseException ignored) {
			return null;
		}
	}
}
