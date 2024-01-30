package helpers.pageable;

import org.opentest4j.MultipleFailuresError;

import java.util.List;
import java.util.Optional;

import static helpers.StringsUtils.collectionToString;

public class PageCheckResult {
    private String descriptionPart;
    private final boolean isFailed;
    private List<?> failedElementList;
    private final AssertionError error;
    private int pageNumber;
    private int checkedElementCount;

    public PageCheckResult(String descriptionPart, List<AssertionError> errorList, List<?> failedElementList, int pageNumber, int checkedElementCount) {
        init(descriptionPart, failedElementList, pageNumber, checkedElementCount);
        this.isFailed = !errorList.isEmpty();
        if(errorList.isEmpty()) {
            error = null;
        } else if (errorList.size() == 1) {
            error = errorList.get(0);
        } else {
            error = new MultipleFailuresError(toString(), errorList);
        }
    }

    public PageCheckResult(String descriptionPart, List<?> failedElementList, int pageNumber, int checkedElementCount) {
        init(descriptionPart, failedElementList, pageNumber, checkedElementCount);
        this.isFailed = !failedElementList.isEmpty();
        this.error = isFailed ? new AssertionError(toString() + "\n" + collectionToString(failedElementList)) : null;
    }

    private void init(String descriptionPart, List<?> failedElementList, int pageNumber, int checkedElementCount) {
        this.descriptionPart = descriptionPart;
        this.failedElementList = failedElementList;
        this.pageNumber = pageNumber;
        this.checkedElementCount = checkedElementCount;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public Optional<AssertionError> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        if (isFailed) {
            return "Стр. " + pageNumber + ". Обнаружено "
                    + failedElementList.size() + " (из " + checkedElementCount + " шт.) элементов, каждый из которых не " + descriptionPart;
        } else {
            return "Стр. " + pageNumber + ". Каждый элемент ("
                    + checkedElementCount + " шт.) " + descriptionPart;
        }
    }
}
