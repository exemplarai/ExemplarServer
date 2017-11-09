package ai.exemplar.storage.impl;

import ai.exemplar.storage.CorrelationsStorageService;
import ai.exemplar.storage.values.LocationCorrelationsEntry;
import ai.exemplar.utils.LocallyCachedS3CSVContent;
import com.amazonaws.services.s3.AmazonS3;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CorrelationsStorageServiceImpl implements CorrelationsStorageService {

    static final Logger log = Logger.getLogger(CorrelationsStorageServiceImpl.class);

    private static final String DATA_IMPORT_BUCKET_NAME = "data-import.exemplar.ai";

    private static final String SALES_CORRELATIONS_FILE_NAME = "sales_correlations.csv";

    private static final String PRICE_CORRELATIONS_FILE_NAME = "price_correlations.csv";

    private final LocallyCachedS3CSVContent<LocationCorrelationsEntry> salesCorrelationsAccessor, priceCorrelationsAccessor;

    @Inject
    public CorrelationsStorageServiceImpl(AmazonS3 amazonS3) {
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        salesCorrelationsAccessor = new LocallyCachedS3CSVContent<>(
                executor, amazonS3,
                DATA_IMPORT_BUCKET_NAME,
                SALES_CORRELATIONS_FILE_NAME,
                LocationCorrelationsEntry.class
        );

        priceCorrelationsAccessor = new LocallyCachedS3CSVContent<>(
                executor, amazonS3,
                DATA_IMPORT_BUCKET_NAME,
                PRICE_CORRELATIONS_FILE_NAME,
                LocationCorrelationsEntry.class
        );
    }

    @Override
    public List<LocationCorrelationsEntry> salesCorrelations() {
        try {
            return salesCorrelationsAccessor.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LocationCorrelationsEntry> priceCorrelations() {
        try {
            return priceCorrelationsAccessor.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
