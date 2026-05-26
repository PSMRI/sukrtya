package com.sukrtya.siwan.forms.dto;

import java.util.List;

import com.sukrtya.siwan.forms.OptionSet;
import com.sukrtya.siwan.forms.OptionValue;

public record OptionSetView(
		Long id,
		String code,
		String name,
		List<OptionValueView> values) {

	public static OptionSetView of(OptionSet set, List<OptionValue> values) {
		return new OptionSetView(
				set.getId(),
				set.getCode(),
				set.getName(),
				values.stream().map(OptionValueView::of).toList());
	}

	public record OptionValueView(String value, String labelEn, String labelHi, int sequence) {

		public static OptionValueView of(OptionValue ov) {
			return new OptionValueView(ov.getValue(), ov.getLabelEn(), ov.getLabelHi(), ov.getSequence());
		}
	}
}
