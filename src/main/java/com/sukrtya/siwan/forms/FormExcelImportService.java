package com.sukrtya.siwan.forms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parses an admin-uploaded forms workbook into {@link Form} / {@link FormQuestion} /
 * {@link OptionSet} / {@link OptionValue} rows. Matches the {@code SukrtyaQuestion.xlsx} dialect.
 *
 * <p><b>Sheet 1 — Question Master</b> (columns, case-insensitive; spaces & punctuation ignored):
 * <ul>
 *   <li>{@code faQID} — integer (required)</li>
 *   <li>{@code Form Name} / {@code formCode} — optional. If provided, normalised & matched against
 *       the form catalog (with built-in aliases for {@code Basic Details}, {@code ANC 1..4}).</li>
 *   <li>{@code faQuestionEN} — English label (required)</li>
 *   <li>{@code faQuestionHI} — Hindi label (optional)</li>
 *   <li>{@code faAnswer} — default value for TEXT/NUMERIC/DATE; for SINGLE/MULTI CHOICE this carries
 *       a slash-separated list of OptionMaster IDs (e.g. {@code 1/2}, {@code 3/4/5}, {@code 19/20}).</li>
 *   <li>{@code skipAnswer} / {@code skiptoQuestion}</li>
 *   <li>{@code faAnswerType} — Text / Numeric / Date / Single Choice / Multi Choice</li>
 *   <li>{@code isMandate} / {@code isMandatory} — 0|1, or {@code -} for computed/auto-filled</li>
 *   <li>{@code maxvalue} / {@code minvalue}</li>
 *   <li>{@code Remarks} — patterns like {@code "LMP Date + 280 Days"} become computed DATE_OFFSET fields</li>
 * </ul>
 *
 * <p><b>Sheet 2 — OptionMaster</b> (flat lookup): {@code OptionID | OptionNameEN | OptionNameReg}.
 * The {@code Reg} column is treated as Hindi/regional label and stored on {@link OptionValue#getLabelHi()}.
 *
 * <p><b>Section detection (in order of precedence):</b>
 * <ol>
 *   <li>Explicit {@code Form Name} cell on the row (after alias resolution).</li>
 *   <li>Inference from the question text: {@code Due date of ... 1st/2nd/3rd/4th ANC} starts section ANC1..4.</li>
 *   <li>Sticky carry-forward from the previous row's section.</li>
 *   <li>Default to {@code BASIC} for rows before any section marker.</li>
 * </ol>
 *
 * <p><b>Auto-generated option sets:</b> for each unique sorted set of OptionMaster IDs referenced
 * in the workbook the parser ensures an {@link OptionSet} exists with a deterministic code
 * (e.g. {@code OPT_1_2}, {@code OPT_3_4_5}) and labels copied from OptionMaster. Each
 * single/multi-choice {@link FormQuestion} then points to that set via {@code option_set_code}.
 */
@Service
public class FormExcelImportService {

	private static final Pattern DATE_OFFSET_PATTERN = Pattern.compile(
			"\\s*([A-Za-z][A-Za-z0-9_ ]+?)\\s*\\+\\s*(\\d+)\\s*Days?\\s*",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern ANC1_PATTERN = Pattern.compile(
			"\\b(1st\\s*anc|first\\s*anc|anc\\s*1\\b|of\\s*1\\s*anc)\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern ANC2_PATTERN = Pattern.compile(
			"\\b(2nd\\s*anc|second\\s*anc|anc\\s*2\\b|of\\s*2\\s*anc)\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern ANC3_PATTERN = Pattern.compile(
			"\\b(3rd\\s*anc|third\\s*anc|anc\\s*3\\b|of\\s*3\\s*anc)\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern ANC4_PATTERN = Pattern.compile(
			"\\b(4th\\s*anc|fourth\\s*anc|anc\\s*4\\b|of\\s*4\\s*anc)\\b", Pattern.CASE_INSENSITIVE);

	private static final String DEFAULT_FORM_CODE = "BASIC";

	/** Best-effort normalisation for free-text Form Name values used in the Excel. */
	private static final Map<String, String> FORM_CODE_ALIASES = Map.ofEntries(
			Map.entry("BASIC", "BASIC"),
			Map.entry("BASICDETAILS", "BASIC"),
			Map.entry("BASICDETAIL", "BASIC"),
			Map.entry("PREGNANTWOMAN", "BASIC"),
			Map.entry("ANC1", "ANC1"),
			Map.entry("ANC2", "ANC2"),
			Map.entry("ANC3", "ANC3"),
			Map.entry("ANC4", "ANC4"),
			Map.entry("FORM1", "BASIC"),
			Map.entry("1STANC", "ANC1"),
			Map.entry("2NDANC", "ANC2"),
			Map.entry("3RDANC", "ANC3"),
			Map.entry("4THANC", "ANC4"),
			Map.entry("FIRSTANC", "ANC1"),
			Map.entry("SECONDANC", "ANC2"),
			Map.entry("THIRDANC", "ANC3"),
			Map.entry("FOURTHANC", "ANC4"));

	private final FormRepository formRepository;
	private final FormQuestionRepository formQuestionRepository;
	private final OptionSetRepository optionSetRepository;
	private final OptionValueRepository optionValueRepository;

	public FormExcelImportService(
			FormRepository formRepository,
			FormQuestionRepository formQuestionRepository,
			OptionSetRepository optionSetRepository,
			OptionValueRepository optionValueRepository) {
		this.formRepository = formRepository;
		this.formQuestionRepository = formQuestionRepository;
		this.optionSetRepository = optionSetRepository;
		this.optionValueRepository = optionValueRepository;
	}

	@Transactional
	public FormImportResult importFormsWorkbook(InputStream inputStream) throws IOException {
		List<String> messages = new ArrayList<>();
		Map<String, Integer> questionsByForm = new LinkedHashMap<>();
		int questionsImported = 0;
		int questionsSkipped = 0;
		int optionsImported = 0;

		try (Workbook workbook = WorkbookFactory.create(inputStream)) {
			DataFormatter fmt = new DataFormatter();

			// Pass 1: load the global OptionMaster lookup (ID -> labels). DB writes happen later.
			Sheet optionSheet = findSheet(workbook, "OptionMaster", "Option Master", "Options");
			Map<Integer, OptionLabel> globalOptions = new LinkedHashMap<>();
			if (optionSheet != null) {
				List<String> optMsgs = loadOptionMaster(optionSheet, fmt, globalOptions);
				messages.addAll(optMsgs);
			}
			else {
				messages.add("OptionMaster sheet not found; single/multi-choice questions will have no options.");
			}

			// Pass 2: walk Question Master, group rows by inferred form code.
			Sheet questionSheet = findSheet(workbook, "Question Master", "QuestionMaster", "Questions");
			if (questionSheet == null) {
				return FormImportResult.error(
						"Required 'Question Master' sheet not found. Available sheets: " + listSheetNames(workbook));
			}
			QuestionImportOutcome q = parseQuestionMaster(questionSheet, fmt, messages);
			questionsSkipped = q.skipped();

			// Pass 3a: build every FormQuestion entity in memory + a global label-to-code index
			// so computed fields can cross form boundaries (e.g. ANC1.due_date <- BASIC.LMP).
			Map<String, String> optionSetCodeByIdList = new HashMap<>();
			Map<Form, List<FormQuestion>> formToBuilt = new LinkedHashMap<>();
			Map<FormQuestion, QuestionRowDraft> draftByFq = new LinkedHashMap<>();
			Map<String, String> globalLabelToCode = new HashMap<>();

			for (Map.Entry<String, List<QuestionRowDraft>> entry : q.byForm().entrySet()) {
				String formCode = entry.getKey();
				List<QuestionRowDraft> drafts = entry.getValue();

				Optional<Form> formOpt = formRepository.findByCodeIgnoreCase(formCode);
				if (formOpt.isEmpty()) {
					questionsSkipped += drafts.size();
					messages.add("Form '" + formCode + "' is not in the catalog; create it via /api/admin/forms before uploading. "
							+ drafts.size() + " question(s) skipped.");
					continue;
				}
				Form form = formOpt.get();

				List<FormQuestion> built = new ArrayList<>(drafts.size());
				int seq = 1;
				for (QuestionRowDraft draft : drafts) {
					FormQuestion fq = new FormQuestion();
					fq.setForm(form);
					fq.setFaQid(draft.faQid());
					fq.setCode(buildQuestionCode(formCode, draft.faQid()));
					fq.setLabelEn(draft.questionEn());
					fq.setLabelHi(draft.questionHi());
					fq.setAnswerType(draft.answerType());
					fq.setMandatory(draft.mandatory());
					fq.setMinValue(draft.minValue());
					fq.setMaxValue(draft.maxValue());
					fq.setSkipValue(draft.skipAnswer());
					fq.setSkipToQid(draft.skipToQuestion());
					fq.setRemarks(draft.remarks());
					fq.setSequence(seq++);

					if (draft.answerType() == AnswerType.SINGLE_CHOICE || draft.answerType() == AnswerType.MULTI_CHOICE) {
						List<Integer> ids = draft.optionIds();
						if (ids != null && !ids.isEmpty()) {
							String setCode = ensureOptionSet(ids, globalOptions, optionSetCodeByIdList, messages, draft);
							fq.setOptionSetCode(setCode);
						}
					}
					else {
						// For non-choice rows, faAnswer (when present) is the default value.
						fq.setDefaultValue(draft.faAnswerRaw());
					}

					built.add(fq);
					draftByFq.put(fq, draft);
					globalLabelToCode.put(simplifyLabel(draft.questionEn()), fq.getCode());
				}
				formToBuilt.put(form, built);
			}

			// Pass 3b: resolve computed fields with the global label index.
			for (Map.Entry<FormQuestion, QuestionRowDraft> e : draftByFq.entrySet()) {
				FormQuestion fq = e.getKey();
				QuestionRowDraft draft = e.getValue();
				if (isBlank(draft.remarks())) {
					continue;
				}
				Matcher m = DATE_OFFSET_PATTERN.matcher(draft.remarks().trim());
				if (m.matches()) {
					int offsetDays = Integer.parseInt(m.group(2));
					String sourceLabel = simplifyLabel(m.group(1));
					String sourceCode = globalLabelToCode.get(sourceLabel);
					if (sourceCode == null) {
						sourceCode = globalLabelToCode.get(simplifyLabel(stripDateSuffix(m.group(1))));
					}
					fq.setComputedKind(ComputedKind.DATE_OFFSET);
					fq.setComputedSourceCode(sourceCode);
					fq.setComputedOffsetDays(offsetDays);
					if (sourceCode == null) {
						messages.add("Question " + fq.getCode() + " (" + fq.getLabelEn()
								+ "): computed source '" + m.group(1).trim() + "' not found in any form; field will need manual entry.");
					}
				}
			}

			// Pass 3c: persist each form's questions and bump its version.
			for (Map.Entry<Form, List<FormQuestion>> e : formToBuilt.entrySet()) {
				Form form = e.getKey();
				List<FormQuestion> built = e.getValue();
				formQuestionRepository.deleteByFormId(form.getId());
				formQuestionRepository.flush();
				formQuestionRepository.saveAll(built);
				form.setVersion(form.getVersion() + 1);
				formRepository.save(form);
				questionsImported += built.size();
				questionsByForm.put(form.getCode(), built.size());
			}

			optionsImported = optionSetCodeByIdList.size();
		}

		return new FormImportResult(true, questionsImported, questionsSkipped, optionsImported,
				questionsByForm, messages);
	}

	// ============================================================================
	// OptionMaster (flat lookup): OptionID | OptionNameEN | OptionNameReg
	// ============================================================================

	private List<String> loadOptionMaster(Sheet sheet, DataFormatter fmt, Map<Integer, OptionLabel> out) {
		List<String> messages = new ArrayList<>();
		int headerRowIndex = findOptionHeaderRow(sheet, fmt);
		if (headerRowIndex < 0) {
			messages.add("OptionMaster: header row not found (expected 'OptionID' or 'optionSetCode').");
			return messages;
		}
		OptionColumnIndices cols = resolveOptionColumns(sheet.getRow(headerRowIndex), fmt);
		if (!cols.isValid()) {
			messages.add("OptionMaster: could not map required columns (OptionID and label).");
			return messages;
		}

		int lastRow = sheet.getLastRowNum();
		for (int r = headerRowIndex + 1; r <= lastRow; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			String idRaw = cellTrim(row, cols.optionId, fmt);
			String labelEn = cellTrim(row, cols.labelEn, fmt);
			String labelHi = cols.labelHi >= 0 ? cellTrim(row, cols.labelHi, fmt) : null;
			if (isBlank(idRaw) && isBlank(labelEn)) {
				continue;
			}
			Integer id = parseIntegerSafe(idRaw);
			if (id == null) {
				continue;
			}
			if (isBlank(labelEn)) {
				continue;
			}
			out.put(id, new OptionLabel(labelEn.trim(), isBlank(labelHi) ? null : labelHi.trim()));
		}
		return messages;
	}

	private static int findOptionHeaderRow(Sheet sheet, DataFormatter fmt) {
		int last = Math.min(sheet.getLastRowNum(), 30);
		for (int r = 0; r <= last; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			short cells = row.getLastCellNum();
			for (int c = 0; c < cells; c++) {
				String h = cellTrim(row, c, fmt).toLowerCase(Locale.ROOT).replace(" ", "");
				if (h.equals("optionid") || h.equals("id") || h.contains("optionset")) {
					return r;
				}
			}
		}
		return -1;
	}

	private static OptionColumnIndices resolveOptionColumns(Row headerRow, DataFormatter fmt) {
		if (headerRow == null) {
			return OptionColumnIndices.invalid();
		}
		int optionId = -1;
		int labelEn = -1;
		int labelHi = -1;
		short last = headerRow.getLastCellNum();
		for (int i = 0; i < last; i++) {
			String h = cellTrim(headerRow, i, fmt).toLowerCase(Locale.ROOT).replace(" ", "");
			if (h.isEmpty()) {
				continue;
			}
			if (optionId < 0 && (h.equals("optionid") || h.equals("id") || h.equals("value"))) {
				optionId = i;
			}
			else if (labelEn < 0 && (h.equals("optionnameen") || h.equals("labelen") || h.equals("label")
					|| h.equals("name") || h.equals("nameen") || h.equals("labelenglish"))) {
				labelEn = i;
			}
			else if (labelHi < 0 && (h.equals("optionnamereg") || h.equals("labelhi") || h.equals("labelregional")
					|| h.equals("namereg") || h.equals("namehi") || h.equals("hindi") || h.equals("regional"))) {
				labelHi = i;
			}
		}
		return new OptionColumnIndices(optionId, labelEn, labelHi);
	}

	private String ensureOptionSet(
			List<Integer> ids,
			Map<Integer, OptionLabel> globalOptions,
			Map<String, String> setCodeCache,
			List<String> messages,
			QuestionRowDraft draft) {
		// Dedup + sort for a deterministic code per unique combination.
		List<Integer> sortedUnique = new ArrayList<>(new TreeSet<>(ids));
		String key = sortedUnique.toString();
		String existing = setCodeCache.get(key);
		if (existing != null) {
			return existing;
		}
		String code = buildOptionSetCode(sortedUnique);
		OptionSet set = optionSetRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
			OptionSet created = new OptionSet();
			created.setCode(code);
			created.setName("Options " + sortedUnique);
			return optionSetRepository.save(created);
		});
		// Wholesale replace values so a re-uploaded OptionMaster propagates label changes.
		optionValueRepository.deleteByOptionSetId(set.getId());
		optionValueRepository.flush();
		int seq = 1;
		List<Integer> missing = new ArrayList<>();
		for (Integer id : sortedUnique) {
			OptionLabel lbl = globalOptions.get(id);
			if (lbl == null) {
				missing.add(id);
				continue;
			}
			OptionValue ov = new OptionValue();
			ov.setOptionSet(set);
			ov.setValue(String.valueOf(id));
			ov.setLabelEn(lbl.labelEn());
			ov.setLabelHi(lbl.labelHi());
			ov.setSequence(seq++);
			optionValueRepository.save(ov);
		}
		if (!missing.isEmpty()) {
			messages.add("Question faQID " + draft.faQid() + " (" + draft.questionEn() + "): OptionMaster missing id(s) "
					+ missing + "; resulting dropdown is incomplete.");
		}
		setCodeCache.put(key, code);
		return code;
	}

	private static String buildOptionSetCode(List<Integer> sortedIds) {
		StringBuilder sb = new StringBuilder("OPT");
		for (Integer id : sortedIds) {
			sb.append('_').append(id);
		}
		if (sb.length() <= 50) {
			return sb.toString();
		}
		// Long lists: keep a stable, readable code (first..last + count) within VARCHAR(50).
		Integer first = sortedIds.get(0);
		Integer lastId = sortedIds.get(sortedIds.size() - 1);
		String shortCode = "OPT_" + first + "_TO_" + lastId + "_N" + sortedIds.size();
		return shortCode.length() > 50 ? shortCode.substring(0, 50) : shortCode;
	}

	// ============================================================================
	// Question Master (sticky section + inference)
	// ============================================================================

	private QuestionImportOutcome parseQuestionMaster(Sheet sheet, DataFormatter fmt, List<String> messages) {
		Map<String, List<QuestionRowDraft>> grouped = new LinkedHashMap<>();
		int headerRowIndex = findQuestionHeaderRow(sheet, fmt);
		if (headerRowIndex < 0) {
			messages.add("Question Master: header row not found (expected 'faQId' / 'faQuestionEN').");
			return new QuestionImportOutcome(0, grouped, messages);
		}
		QuestionColumnIndices cols = resolveQuestionColumns(sheet.getRow(headerRowIndex), fmt);
		if (!cols.isValid()) {
			messages.add("Question Master: could not map required columns (faQId, faQuestionEN, faAnswerType).");
			return new QuestionImportOutcome(0, grouped, messages);
		}

		String currentFormCode = null;
		int skipped = 0;
		int lastRow = sheet.getLastRowNum();
		for (int r = headerRowIndex + 1; r <= lastRow; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			int excelRow = r + 1;

			String qidRaw = cellTrim(row, cols.faQid, fmt);
			String questionEn = cellTrim(row, cols.questionEn, fmt);
			String answerTypeRaw = cols.answerType >= 0 ? cellTrim(row, cols.answerType, fmt) : "";

			if (isBlank(qidRaw) && isBlank(questionEn)) {
				continue;
			}
			if (isBlank(answerTypeRaw)) {
				// Geography / staff metadata rows in the user's workbook (District, Block, ...).
				// Silently ignore: they don't belong to any survey form.
				continue;
			}

			Integer qid = parseIntegerSafe(qidRaw);
			if (qid == null) {
				skipped++;
				messages.add("Question Master row " + excelRow + ": skipped (faQId is not numeric: '" + qidRaw + "').");
				continue;
			}
			if (isBlank(questionEn)) {
				skipped++;
				messages.add("Question Master row " + excelRow + ": skipped (faQuestionEN is empty).");
				continue;
			}
			AnswerType answerType = parseAnswerType(answerTypeRaw);
			if (answerType == null) {
				skipped++;
				messages.add("Question Master row " + excelRow + ": skipped (unknown faAnswerType '" + answerTypeRaw + "').");
				continue;
			}

			// Section resolution: explicit Form Name -> inference -> sticky -> BASIC default.
			String formNameRaw = cols.formCode >= 0 ? cellTrim(row, cols.formCode, fmt) : "";
			String resolved = resolveFormCode(formNameRaw, questionEn, currentFormCode);
			if (resolved != null) {
				currentFormCode = resolved;
			}
			if (currentFormCode == null) {
				currentFormCode = DEFAULT_FORM_CODE;
			}

			String mandateRaw = cols.mandatory >= 0 ? cellTrim(row, cols.mandatory, fmt) : "";
			boolean mandatory = parseBoolean(mandateRaw);

			String faAnswerRaw = cols.defaultAnswer >= 0 ? cellTrimOrNull(row, cols.defaultAnswer, fmt) : null;
			List<Integer> optionIds = (answerType == AnswerType.SINGLE_CHOICE || answerType == AnswerType.MULTI_CHOICE)
					? parseOptionIds(faAnswerRaw)
					: List.of();

			QuestionRowDraft draft = new QuestionRowDraft(
					currentFormCode,
					qid,
					questionEn.trim(),
					cols.questionHi >= 0 ? cellTrimOrNull(row, cols.questionHi, fmt) : null,
					answerType,
					mandatory,
					cols.minValue >= 0 ? cellTrimOrNull(row, cols.minValue, fmt) : null,
					cols.maxValue >= 0 ? cellTrimOrNull(row, cols.maxValue, fmt) : null,
					faAnswerRaw,
					optionIds,
					cols.skipAnswer >= 0 ? cellTrimOrNull(row, cols.skipAnswer, fmt) : null,
					cols.skipToQuestion >= 0 ? parseIntegerSafe(cellTrim(row, cols.skipToQuestion, fmt)) : null,
					cols.remarks >= 0 ? cellTrimOrNull(row, cols.remarks, fmt) : null);

			grouped.computeIfAbsent(draft.formCode(), k -> new ArrayList<>()).add(draft);
		}
		return new QuestionImportOutcome(skipped, grouped, messages);
	}

	private static String resolveFormCode(String rawCellValue, String questionEn, String previous) {
		// 1. Explicit Form Name cell wins (with alias resolution).
		if (!isBlank(rawCellValue)) {
			String norm = normaliseFormCode(rawCellValue);
			String mapped = FORM_CODE_ALIASES.getOrDefault(norm, norm);
			if (mapped != null && !mapped.isEmpty()) {
				return mapped;
			}
		}
		// 2. Inference from question text: only triggers on section-header patterns like
		//    "Due date of ... 1st/2nd/3rd/4th ANC". Avoid over-matching plain ANC fields.
		String inferred = inferSectionFromQuestion(questionEn);
		if (inferred != null) {
			return inferred;
		}
		// 3. Carry forward the previous section (or null -> caller will default to BASIC).
		return previous;
	}

	private static String inferSectionFromQuestion(String label) {
		if (label == null) {
			return null;
		}
		String lower = label.toLowerCase(Locale.ROOT);
		// Only trigger on rows that look like "due date of <N> ANC" - the actual section markers in the sheet.
		if (!lower.contains("due date") && !lower.contains("due  date")) {
			return null;
		}
		if (ANC4_PATTERN.matcher(lower).find()) {
			return "ANC4";
		}
		if (ANC3_PATTERN.matcher(lower).find()) {
			return "ANC3";
		}
		if (ANC2_PATTERN.matcher(lower).find()) {
			return "ANC2";
		}
		if (ANC1_PATTERN.matcher(lower).find()) {
			return "ANC1";
		}
		return null;
	}

	private static String normaliseFormCode(String raw) {
		if (raw == null) {
			return "";
		}
		return raw.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
	}

	private static List<Integer> parseOptionIds(String raw) {
		if (isBlank(raw)) {
			return List.of();
		}
		// Tolerate slashes, commas, semicolons, spaces.
		String[] tokens = raw.trim().split("[/,;\\s]+");
		LinkedHashSet<Integer> out = new LinkedHashSet<>();
		for (String t : tokens) {
			Integer v = parseIntegerSafe(t);
			if (v != null) {
				out.add(v);
			}
		}
		return new ArrayList<>(out);
	}

	private static int findQuestionHeaderRow(Sheet sheet, DataFormatter fmt) {
		int last = Math.min(sheet.getLastRowNum(), 30);
		for (int r = 0; r <= last; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			short cells = row.getLastCellNum();
			for (int c = 0; c < cells; c++) {
				String h = cellTrim(row, c, fmt).toLowerCase(Locale.ROOT).replace(" ", "");
				if (h.equals("faqid") || h.equals("faquestionid") || h.equals("qid")) {
					return r;
				}
			}
		}
		return -1;
	}

	private static QuestionColumnIndices resolveQuestionColumns(Row headerRow, DataFormatter fmt) {
		if (headerRow == null) {
			return QuestionColumnIndices.invalid();
		}
		int formCode = -1;
		int faQid = -1;
		int questionEn = -1;
		int questionHi = -1;
		int defaultAnswer = -1;
		int skipAnswer = -1;
		int skipToQuestion = -1;
		int answerType = -1;
		int mandatory = -1;
		int maxValue = -1;
		int minValue = -1;
		int remarks = -1;
		short last = headerRow.getLastCellNum();
		for (int i = 0; i < last; i++) {
			String h = cellTrim(headerRow, i, fmt).toLowerCase(Locale.ROOT).replace(" ", "");
			if (h.isEmpty()) {
				continue;
			}
			if (formCode < 0 && (h.equals("formcode") || h.equals("form") || h.equals("formname")
					|| h.equals("section") || h.equals("sectioncode"))) {
				formCode = i;
			}
			else if (faQid < 0 && (h.equals("faqid") || h.equals("faquestionid") || h.equals("qid"))) {
				faQid = i;
			}
			else if (questionEn < 0 && (h.equals("faquestionen") || h.equals("questionen")
					|| h.equals("question") || h.equals("faquestion"))) {
				questionEn = i;
			}
			else if (questionHi < 0 && (h.equals("faquestionhi") || h.equals("questionhi")
					|| h.equals("faquestionreg") || h.equals("questionreg"))) {
				questionHi = i;
			}
			else if (defaultAnswer < 0 && (h.equals("faanswer") || h.equals("defaultanswer") || h.equals("answer"))) {
				defaultAnswer = i;
			}
			else if (skipAnswer < 0 && (h.equals("skipanswer") || h.equals("skipifvalue") || h.equals("skipif"))) {
				skipAnswer = i;
			}
			else if (skipToQuestion < 0 && (h.equals("skiptoquestion") || h.equals("skipto") || h.equals("skiptoqid"))) {
				skipToQuestion = i;
			}
			else if (answerType < 0 && (h.equals("faanswertype") || h.equals("answertype") || h.equals("type"))) {
				answerType = i;
			}
			else if (mandatory < 0 && (h.equals("ismandatory") || h.equals("ismandate")
					|| h.equals("mandatory") || h.equals("required"))) {
				mandatory = i;
			}
			else if (maxValue < 0 && (h.equals("maxvalue") || h.equals("max"))) {
				maxValue = i;
			}
			else if (minValue < 0 && (h.equals("minvalue") || h.equals("min"))) {
				minValue = i;
			}
			else if (remarks < 0 && (h.equals("remarks") || h.equals("remark") || h.equals("note")
					|| h.equals("notes") || h.equals("formula"))) {
				remarks = i;
			}
		}
		return new QuestionColumnIndices(formCode, faQid, questionEn, questionHi, defaultAnswer,
				skipAnswer, skipToQuestion, answerType, mandatory, maxValue, minValue, remarks);
	}

	// ============================================================================
	// Helpers
	// ============================================================================

	private static Sheet findSheet(Workbook workbook, String... candidates) {
		for (String name : candidates) {
			Sheet s = workbook.getSheet(name);
			if (s != null) {
				return s;
			}
		}
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet s = workbook.getSheetAt(i);
			String sn = s.getSheetName() != null ? s.getSheetName().toLowerCase(Locale.ROOT) : "";
			for (String candidate : candidates) {
				if (sn.replace(" ", "").equals(candidate.toLowerCase(Locale.ROOT).replace(" ", ""))) {
					return s;
				}
			}
		}
		return null;
	}

	private static String listSheetNames(Workbook workbook) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			names.add(workbook.getSheetName(i));
		}
		return names.toString();
	}

	private static String cellTrim(Row row, int colIndex, DataFormatter fmt) {
		if (row == null || colIndex < 0) {
			return "";
		}
		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			return "";
		}
		return fmt.formatCellValue(cell).trim();
	}

	private static String cellTrimOrNull(Row row, int colIndex, DataFormatter fmt) {
		String v = cellTrim(row, colIndex, fmt);
		// Treat blank / literal "NULL" / placeholder "-" as a missing value so callers get clean
		// nulls in the DB instead of stringy sentinels (the user's workbook uses both).
		return isBlank(v) ? null : v;
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty() || "null".equalsIgnoreCase(s.trim()) || "-".equals(s.trim());
	}

	private static Integer parseIntegerSafe(String s) {
		if (isBlank(s)) {
			return null;
		}
		try {
			return Integer.parseInt(s.trim());
		}
		catch (NumberFormatException ignored) {
			try {
				return (int) Double.parseDouble(s.trim());
			}
			catch (NumberFormatException ignored2) {
				return null;
			}
		}
	}

	private static boolean parseBoolean(String s) {
		if (s == null) {
			return false;
		}
		String t = s.trim().toLowerCase(Locale.ROOT);
		// "-" means "computed / not applicable" in the user's workbook -> treat as not mandatory.
		if (t.isEmpty() || t.equals("-") || "null".equals(t)) {
			return false;
		}
		return t.equals("1") || t.equals("true") || t.equals("yes") || t.equals("y");
	}

	private static AnswerType parseAnswerType(String s) {
		if (s == null || s.trim().isEmpty()) {
			return null;
		}
		String t = s.trim().toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "");
		return switch (t) {
			case "text", "string" -> AnswerType.TEXT;
			case "numeric", "number", "int", "integer", "decimal", "float", "double" -> AnswerType.NUMERIC;
			case "date" -> AnswerType.DATE;
			case "singlechoice", "single", "radio", "dropdown", "select" -> AnswerType.SINGLE_CHOICE;
			case "multichoice", "multi", "multiple", "checkbox", "checkboxes" -> AnswerType.MULTI_CHOICE;
			default -> null;
		};
	}

	/** Build the stable JSON answer key for a question: {@code <FORM_CODE>_<faQid>} (e.g. {@code ANC1_19}). */
	private static String buildQuestionCode(String formCode, int faQid) {
		return formCode + "_" + faQid;
	}

	private static String simplifyLabel(String label) {
		if (label == null) {
			return "";
		}
		return label.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
	}

	private static String stripDateSuffix(String label) {
		if (label == null) {
			return "";
		}
		return label.replaceAll("(?i)\\bdate\\b", "").trim();
	}

	// ============================================================================
	// Records (internal DTOs)
	// ============================================================================

	private record OptionLabel(String labelEn, String labelHi) {
	}

	private record QuestionImportOutcome(
			int skipped,
			Map<String, List<QuestionRowDraft>> byForm,
			List<String> messages) {
	}

	private record QuestionRowDraft(
			String formCode,
			int faQid,
			String questionEn,
			String questionHi,
			AnswerType answerType,
			boolean mandatory,
			String minValue,
			String maxValue,
			String faAnswerRaw,
			List<Integer> optionIds,
			String skipAnswer,
			Integer skipToQuestion,
			String remarks) {
	}

	private record OptionColumnIndices(int optionId, int labelEn, int labelHi) {

		static OptionColumnIndices invalid() {
			return new OptionColumnIndices(-1, -1, -1);
		}

		boolean isValid() {
			return optionId >= 0 && labelEn >= 0;
		}
	}

	private record QuestionColumnIndices(
			int formCode,
			int faQid,
			int questionEn,
			int questionHi,
			int defaultAnswer,
			int skipAnswer,
			int skipToQuestion,
			int answerType,
			int mandatory,
			int maxValue,
			int minValue,
			int remarks) {

		static QuestionColumnIndices invalid() {
			return new QuestionColumnIndices(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);
		}

		boolean isValid() {
			return faQid >= 0 && questionEn >= 0 && answerType >= 0;
		}
	}

	@SuppressWarnings("unused")
	private static String safeJoin(Collection<?> values) {
		return values == null ? "[]" : values.toString();
	}
}
