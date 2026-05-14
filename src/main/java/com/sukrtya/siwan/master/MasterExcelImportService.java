package com.sukrtya.siwan.master;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sukrtya.siwan.portal.PortalUserProvisioningService;

@Service
public class MasterExcelImportService {

	private final DistrictRepository districtRepository;
	private final BlockRepository blockRepository;
	private final FacilityRepository facilityRepository;
	private final HealthWorkerRepository healthWorkerRepository;
	private final FacilityWorkerAssignmentRepository assignmentRepository;
	private final PortalUserProvisioningService portalUserProvisioningService;

	public MasterExcelImportService(
			DistrictRepository districtRepository,
			BlockRepository blockRepository,
			FacilityRepository facilityRepository,
			HealthWorkerRepository healthWorkerRepository,
			FacilityWorkerAssignmentRepository assignmentRepository,
			PortalUserProvisioningService portalUserProvisioningService) {
		this.districtRepository = districtRepository;
		this.blockRepository = blockRepository;
		this.facilityRepository = facilityRepository;
		this.healthWorkerRepository = healthWorkerRepository;
		this.assignmentRepository = assignmentRepository;
		this.portalUserProvisioningService = portalUserProvisioningService;
	}

	@Transactional
	public MasterImportResult importMasterWorkbook(InputStream inputStream) throws IOException {
		List<String> messages = new ArrayList<>();
		int rowsImported = 0;
		int rowsSkipped = 0;

		try (Workbook workbook = WorkbookFactory.create(inputStream)) {
			Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
			if (sheet == null) {
				return MasterImportResult.error("Workbook has no sheets.");
			}

			DataFormatter formatter = new DataFormatter();
			int headerRowIndex = findHeaderRowIndex(sheet, formatter);
			if (headerRowIndex < 0) {
				return MasterImportResult.error("Could not find a header row (expected columns like District, Block, HWC).");
			}

			ColumnIndices cols = resolveColumns(sheet.getRow(headerRowIndex), formatter);
			if (!cols.isValid()) {
				return MasterImportResult.error("Could not map required columns from header row " + (headerRowIndex + 1) + ".");
			}

			Row headerRow = sheet.getRow(headerRowIndex);
			ExcelHeaderSnapshot excelHeaders = ExcelHeaderSnapshot.fromHeaderRow(headerRow, cols, formatter);

			int lastRow = sheet.getLastRowNum();
			for (int r = headerRowIndex + 1; r <= lastRow; r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				String districtName = cellTrim(row, cols.district, formatter);
				String blockName = cellTrim(row, cols.block, formatter);
				String facilityName = cellTrim(row, cols.hwc, formatter);
				if (isBlank(districtName) && isBlank(blockName) && isBlank(facilityName)) {
					continue;
				}
				int excelRow = r + 1;
				if (isBlank(districtName) || isBlank(blockName) || isBlank(facilityName)) {
					rowsSkipped++;
					messages.add("Row " + excelRow + ": skipped (missing district, block, or HWC facility name).");
					continue;
				}

				District district = findOrCreateDistrict(districtName);
				Block block = findOrCreateBlock(district, blockName);
				Facility facility = findOrCreateFacility(block, facilityName);

				String choName = cellTrim(row, cols.choName, formatter);
				String choMobile = cellTrim(row, cols.choMobile, formatter);
				if (!isNoStaffPlaceholder(choName)) {
					HealthWorker cho = findOrCreateHealthWorker(choName, choMobile);
					replaceSingletonAssignment(facility, cho, HealthWorkerRole.CHO, excelHeaders.choName());
					String choMobileNorm = normalizeMobile(choMobile);
					if (isBlank(choMobileNorm)) {
						messages.add("Row " + excelRow + ": CHO data-collector login skipped (CHO mobile missing or invalid).");
					}
					else {
						portalUserProvisioningService.ensureChoDataCollectorForFacility(choMobileNorm, cho.getFullName(), facility);
					}
				}

				String anmName = cellTrim(row, cols.anmName, formatter);
				String anmMobile = cellTrim(row, cols.anmMobile, formatter);
				if (!isNoStaffPlaceholder(anmName)) {
					HealthWorker anm = findOrCreateHealthWorker(anmName, anmMobile);
					replaceSingletonAssignment(facility, anm, HealthWorkerRole.ANM, excelHeaders.anmName());
				}

				String facilitatorName = cellTrim(row, cols.facilitatorName, formatter);
				String facilitatorMobile = cellTrim(row, cols.facilitatorMobile, formatter);
				if (isBlank(facilitatorName)) {
					rowsSkipped++;
					messages.add("Row " + excelRow + ": skipped (missing ASHA facilitator name).");
					continue;
				}
				HealthWorker facilitatorWorker = findOrCreateHealthWorker(facilitatorName, facilitatorMobile);
				FacilityWorkerAssignment facilitatorAssignment = findOrCreateFacilitatorAssignment(
						facility, facilitatorWorker, excelHeaders.facilitatorName());
				assignmentRepository.saveAndFlush(facilitatorAssignment);

				String ashaName = cellTrim(row, cols.ashaName, formatter);
				String ashaMobile = cellTrim(row, cols.ashaMobile, formatter);
				if (isBlank(ashaName)) {
					rowsSkipped++;
					messages.add("Row " + excelRow + ": skipped (missing ASHA name).");
					continue;
				}
				HealthWorker ashaWorker = findOrCreateHealthWorker(ashaName, ashaMobile);
				if (assignmentRepository.countActiveAssignment(
						facility.getId(), ashaWorker.getId(), HealthWorkerRole.ASHA, FacilityAssignmentStatus.ACTIVE) > 0) {
					messages.add("Row " + excelRow + ": ASHA already active for this facility; left unchanged.");
					rowsImported++;
					continue;
				}

				FacilityWorkerAssignment ashaAssignment = new FacilityWorkerAssignment();
				ashaAssignment.setFacility(facility);
				ashaAssignment.setHealthWorker(ashaWorker);
				ashaAssignment.setRole(HealthWorkerRole.ASHA);
				ashaAssignment.setStatus(FacilityAssignmentStatus.ACTIVE);
				ashaAssignment.setSupervisorAssignment(facilitatorAssignment);
				ashaAssignment.setStaffType(excelHeaders.ashaName());
				assignmentRepository.save(ashaAssignment);
				rowsImported++;
			}
		}

		return new MasterImportResult(true, rowsImported, rowsSkipped, messages);
	}

	private static int findHeaderRowIndex(Sheet sheet, DataFormatter formatter) {
		int last = Math.min(sheet.getLastRowNum(), 30);
		for (int r = 0; r <= last; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			String c1 = cellTrim(row, 1, formatter).toLowerCase(Locale.ROOT);
			String c0 = cellTrim(row, 0, formatter).toLowerCase(Locale.ROOT);
			if (c1.contains("district") || c0.contains("district")) {
				return r;
			}
		}
		return -1;
	}

	private static ColumnIndices resolveColumns(Row headerRow, DataFormatter formatter) {
		if (headerRow == null) {
			return ColumnIndices.invalid();
		}
		String colB = cellTrim(headerRow, 1, formatter).toLowerCase(Locale.ROOT);
		if (colB.contains("district")) {
			return new ColumnIndices(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
		}

		short last = headerRow.getLastCellNum();
		int district = -1;
		int block = -1;
		int hwc = -1;
		int choName = -1;
		int choMobile = -1;
		int anmName = -1;
		int anmMobile = -1;
		int facilitatorName = -1;
		int facilitatorMobile = -1;
		int ashaName = -1;
		int ashaMobile = -1;

		List<Integer> mobileCols = new ArrayList<>();
		for (int i = 0; i < last; i++) {
			String h = cellTrim(headerRow, i, formatter).toLowerCase(Locale.ROOT);
			if (h.isEmpty()) {
				continue;
			}
			if (h.contains("district") && district < 0) {
				district = i;
			}
			else if (h.contains("block") && block < 0) {
				block = i;
			}
			else if ((h.contains("hwc") || h.contains("facility")) && hwc < 0) {
				hwc = i;
			}
			else if (h.contains("cho") && h.contains("name") && choName < 0) {
				choName = i;
			}
			else if (h.contains("cho") && h.contains("mobile") && choMobile < 0) {
				choMobile = i;
			}
			else if (h.contains("anm") && h.contains("name") && anmName < 0) {
				anmName = i;
			}
			else if (h.contains("anm") && h.contains("mobile") && anmMobile < 0) {
				anmMobile = i;
			}
			else if (h.contains("facilitat") && facilitatorName < 0) {
				facilitatorName = i;
			}
			else if (h.contains("asha") && h.contains("name") && ashaName < 0) {
				ashaName = i;
			}
			else if (h.contains("mobile")) {
				mobileCols.add(i);
			}
		}

		if (facilitatorName >= 0 && ashaName >= 0 && !mobileCols.isEmpty()) {
			for (int i = 0; i < mobileCols.size(); i++) {
				if (mobileCols.get(i) > facilitatorName && mobileCols.get(i) < ashaName) {
					facilitatorMobile = mobileCols.get(i);
					break;
				}
			}
			if (facilitatorMobile < 0) {
				for (int mc : mobileCols) {
					if (mc > facilitatorName && mc < ashaName) {
						facilitatorMobile = mc;
						break;
					}
				}
			}
			for (int i = mobileCols.size() - 1; i >= 0; i--) {
				int mc = mobileCols.get(i);
				if (mc > ashaName) {
					ashaMobile = mc;
					break;
				}
			}
			if (ashaMobile < 0 && facilitatorMobile >= 0 && mobileCols.size() >= 2) {
				ashaMobile = mobileCols.get(mobileCols.size() - 1);
				if (ashaMobile == facilitatorMobile && mobileCols.size() > 1) {
					ashaMobile = mobileCols.get(mobileCols.size() - 2);
				}
			}
		}

		ColumnIndices mapped = new ColumnIndices(
				district, block, hwc, choName, choMobile, anmName, anmMobile,
				facilitatorName, facilitatorMobile, ashaName, ashaMobile);
		if (mapped.isValid()) {
			return mapped;
		}
		return ColumnIndices.invalid();
	}

	private District findOrCreateDistrict(String name) {
		String n = name.trim();
		return districtRepository.findFirstByNameIgnoreCase(n).orElseGet(() -> {
			District d = new District();
			d.setName(n);
			d.setActive(true);
			return districtRepository.save(d);
		});
	}

	private Block findOrCreateBlock(District district, String name) {
		String n = name.trim();
		return blockRepository.findByDistrictIdAndNameIgnoreCase(district.getId(), n).orElseGet(() -> {
			Block b = new Block();
			b.setDistrict(district);
			b.setName(n);
			b.setActive(true);
			return blockRepository.save(b);
		});
	}

	private Facility findOrCreateFacility(Block block, String name) {
		String n = name.trim();
		return facilityRepository.findByBlockIdAndNameIgnoreCase(block.getId(), n)
				.map(f -> ensureFacilityCodeIfBlank(f))
				.orElseGet(() -> {
					Facility f = new Facility();
					f.setBlock(block);
					f.setName(n);
					f.setFacilityType("HWC");
					f.setActive(true);
					Facility saved = facilityRepository.save(f);
					return ensureFacilityCodeIfBlank(saved);
				});
	}

	/** When {@code facility.code} is null or blank, assign a unique 10-digit numeric code. */
	private Facility ensureFacilityCodeIfBlank(Facility facility) {
		if (facility.getCode() != null && !facility.getCode().trim().isEmpty()) {
			return facility;
		}
		String code = generateUniqueTenDigitFacilityCode();
		facility.setCode(code);
		return facilityRepository.save(facility);
	}

	/**
	 * Random integer in [1_000_000_000, 9_999_999_999] so the string is always 10 digits; retries if {@code code} already exists.
	 */
	private String generateUniqueTenDigitFacilityCode() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		for (int attempt = 0; attempt < 80; attempt++) {
			String code = Long.toString(rnd.nextLong(1_000_000_000L, 10_000_000_000L));
			if (!facilityRepository.existsByCode(code)) {
				return code;
			}
		}
		// Extremely unlikely: fall back to time-based 10 digits
		long suffix = System.nanoTime() % 1_000_000_000L;
		String code = "9" + String.format("%09d", Math.abs(suffix));
		if (facilityRepository.existsByCode(code)) {
			code = "8" + String.format("%09d", rnd.nextInt(1_000_000_000));
		}
		return code;
	}

	private HealthWorker findOrCreateHealthWorker(String name, String mobileRaw) {
		String n = name.trim();
		String mobile = normalizeMobile(mobileRaw);
		if (!isBlank(mobile)) {
			Optional<HealthWorker> byMobile = healthWorkerRepository.findFirstByMobilePhone(mobile);
			if (byMobile.isPresent()) {
				HealthWorker w = byMobile.get();
				if (!w.getFullName().equalsIgnoreCase(n)) {
					w.setFullName(n);
					return healthWorkerRepository.save(w);
				}
				return w;
			}
		}
		if (isBlank(mobile)) {
			return healthWorkerRepository.findFirstByFullNameIgnoreCaseAndMobilePhoneIsNull(n).orElseGet(() -> {
				HealthWorker w = new HealthWorker();
				w.setFullName(n);
				w.setMobilePhone(null);
				w.setActive(true);
				return healthWorkerRepository.save(w);
			});
		}
		HealthWorker w = new HealthWorker();
		w.setFullName(n);
		w.setMobilePhone(mobile);
		w.setActive(true);
		return healthWorkerRepository.save(w);
	}

	private void replaceSingletonAssignment(
			Facility facility,
			HealthWorker worker,
			HealthWorkerRole role,
			String staffTypeLabel) {
		Optional<FacilityWorkerAssignment> current = assignmentRepository.findActiveSingletonRole(
				facility.getId(), role, FacilityAssignmentStatus.ACTIVE);
		if (current.isPresent() && current.get().getHealthWorker().getId().equals(worker.getId())) {
			if (!isBlank(staffTypeLabel)) {
				FacilityWorkerAssignment a = current.get();
				a.setStaffType(staffTypeLabel);
				assignmentRepository.save(a);
			}
			return;
		}
		current.ifPresent(a -> {
			a.setStatus(FacilityAssignmentStatus.UNMAPPED);
			assignmentRepository.saveAndFlush(a);
		});
		FacilityWorkerAssignment created = new FacilityWorkerAssignment();
		created.setFacility(facility);
		created.setHealthWorker(worker);
		created.setRole(role);
		created.setStatus(FacilityAssignmentStatus.ACTIVE);
		if (!isBlank(staffTypeLabel)) {
			created.setStaffType(staffTypeLabel);
		}
		assignmentRepository.save(created);
	}

	private FacilityWorkerAssignment findOrCreateFacilitatorAssignment(
			Facility facility,
			HealthWorker facilitatorWorker,
			String staffTypeLabel) {
		return assignmentRepository
				.findActiveAssignment(
						facility.getId(),
						facilitatorWorker.getId(),
						HealthWorkerRole.ASHA_FACILITATOR,
						FacilityAssignmentStatus.ACTIVE)
				.map(existing -> {
					if (!isBlank(staffTypeLabel)) {
						existing.setStaffType(staffTypeLabel);
						return assignmentRepository.save(existing);
					}
					return existing;
				})
				.orElseGet(() -> {
					FacilityWorkerAssignment a = new FacilityWorkerAssignment();
					a.setFacility(facility);
					a.setHealthWorker(facilitatorWorker);
					a.setRole(HealthWorkerRole.ASHA_FACILITATOR);
					a.setStatus(FacilityAssignmentStatus.ACTIVE);
					if (!isBlank(staffTypeLabel)) {
						a.setStaffType(staffTypeLabel);
					}
					return assignmentRepository.save(a);
				});
	}

	private static String cellTrim(Row row, int colIndex, DataFormatter formatter) {
		if (row == null) {
			return "";
		}
		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			return "";
		}
		return formatter.formatCellValue(cell).trim();
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private static boolean isNoStaffPlaceholder(String name) {
		if (isBlank(name)) {
			return true;
		}
		String compact = name.trim().replace(" ", "").toLowerCase(Locale.ROOT);
		return compact.equals("nocho") || compact.equals("noanm");
	}

	private static String normalizeMobile(String raw) {
		if (isBlank(raw)) {
			return null;
		}
		String digits = raw.replaceAll("\\D+", "");
		if (digits.isEmpty()) {
			return null;
		}
		if (digits.length() > 10) {
			digits = digits.substring(digits.length() - 10);
		}
		return digits;
	}

	/** Trimmed Excel header cell text for each staff name column (identifies slot in the sheet). */
	private record ExcelHeaderSnapshot(String choName, String anmName, String facilitatorName, String ashaName) {

		static ExcelHeaderSnapshot fromHeaderRow(Row headerRow, ColumnIndices cols, DataFormatter fmt) {
			if (headerRow == null) {
				return defaults();
			}
			return new ExcelHeaderSnapshot(
					pick(headerRow, cols.choName, fmt, "CHO Name"),
					pick(headerRow, cols.anmName, fmt, "ANM Name"),
					pick(headerRow, cols.facilitatorName, fmt, "ASHA Facilitator"),
					pick(headerRow, cols.ashaName, fmt, "Asha Name"));
		}

		private static ExcelHeaderSnapshot defaults() {
			return new ExcelHeaderSnapshot("CHO Name", "ANM Name", "ASHA Facilitator", "Asha Name");
		}

		private static String pick(Row headerRow, int col, DataFormatter fmt, String fallback) {
			String v = cellTrim(headerRow, col, fmt);
			return isBlank(v) ? fallback : v;
		}
	}

	private record ColumnIndices(
			int district,
			int block,
			int hwc,
			int choName,
			int choMobile,
			int anmName,
			int anmMobile,
			int facilitatorName,
			int facilitatorMobile,
			int ashaName,
			int ashaMobile) {

		static ColumnIndices invalid() {
			return new ColumnIndices(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);
		}

		boolean isValid() {
			return district >= 0 && block >= 0 && hwc >= 0
					&& facilitatorName >= 0 && facilitatorMobile >= 0
					&& ashaName >= 0 && ashaMobile >= 0;
		}
	}
}
