package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

@ActiveProfiles({"test", "google-cloud"})
public class FileServiceCloudStorageIntegrationTest extends AtlasDatabaseTestBase {

    @Autowired
    FileService fileServiceCloudStorage;

    @Autowired
    Storage storage;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ImplementationRepository implementationRepository;

    @Mock
    Blob mockBlob;

    @Test
    public void givenFileNotExists_WhenCreate_ThenShouldBeCreatedAndLinkedToImplementation() {
        // Given
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenReturn(mockBlob);
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
        assertThat(fileRepository.findAll().size()).isEqualTo(0);

        //When
        File createdFile =
            fileServiceCloudStorage.create(persistedImplementation.getId(), getMultipartFile());

        //Then
        assertThat(fileRepository.findAll().size()).isEqualTo(1);
        assertThat(fileRepository.findById(createdFile.getId())).isPresent();
        assertThat(createdFile.getImplementation().getId()).isEqualTo(persistedImplementation.getId());
    }

    @Test
    public void givenNone_WhenCreateAndStorageExceptionIsThrown_ThenCatchAndThrowCloudStorageException() {
        // Given
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenThrow(StorageException.class);

        // When
        Assertions.assertThrows(CloudStorageException.class,
            () -> fileServiceCloudStorage.create(persistedImplementation.getId(), getMultipartFile()));
    }

    @Test
    public void givenFileExists_whenFindById_ThenShouldReturnFile() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());
        // When Then
        assertThat(fileServiceCloudStorage.findById(persistedFile.getId()))
            .isEqualToComparingFieldByField(persistedFile);
    }

    @Test
    public void givenFilesOfImplementationExists_whenFindAllByImplementationId_thenShouldReturnAllFilesOfImpl() {
        // Given
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
        File file = getDummyFile();
        File fileTwo = getDummyFile();
        file.setImplementation(persistedImplementation);
        fileTwo.setImplementation(persistedImplementation);
        fileRepository.save(file);
        fileRepository.save(fileTwo);

        // When
        Page<File> implementationFiles =
            fileServiceCloudStorage.findAllByImplementationId(persistedImplementation.getId(), PageRequest.of(1, 10));

        // Then
        assertThat(implementationFiles.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void delete() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());

        // When
        when(storage.delete(Mockito.any(BlobId.class))).thenReturn(true);
        fileServiceCloudStorage.delete(persistedFile.getId());

        //Then
        assertThat(fileRepository.findById(persistedFile.getId())).isNotPresent();
    }

    private File getDummyFile() {
        File file = new File();
        file.setName("Test");
        file.setFileURL("implId/fileId" + Math.random());
        return file;
    }

    private MultipartFile getMultipartFile() {
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = generateRandomByteArray();
        return new MockMultipartFile(name,
            originalFileName, contentType, content);
    }

    private Implementation getDummyImplementation() {
        Implementation dummyImplementation = new Implementation();
        dummyImplementation.setName("dummy Impl");
        return dummyImplementation;
    }

    private byte[] generateRandomByteArray() {
        Random rd = new Random();
        byte[] arr = new byte[7];
        rd.nextBytes(arr);
        return arr;
    }


}
