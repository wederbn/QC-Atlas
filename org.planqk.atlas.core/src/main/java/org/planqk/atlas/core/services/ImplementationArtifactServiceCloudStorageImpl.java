package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationArtifact;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import lombok.AllArgsConstructor;

@Service
@Profile("stoneOne")
@AllArgsConstructor
public class ImplementationArtifactServiceCloudStorageImpl implements ImplementationArtifactService {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    private final String implementationArtifactsBucketName = "planqk-algo-artifacts";

    private final ImplementationArtifactRepository implementationArtifactRepository;

    private final ImplementationRepository implementationRepository;

    private final Bucket bucket = StorageOptions.getDefaultInstance().getService().get(implementationArtifactsBucketName);

    @Override
    public ImplementationArtifact create(UUID implementationId, MultipartFile file) {
        try {
            Blob blob = this.bucket.create(implementationId + "/" + file.getOriginalFilename(), file.getBytes(), file.getContentType());
            ImplementationArtifact implementationArtifact = getImplementationArtifactFromBlob(blob);
            Optional<ImplementationArtifact> persistedImplementationArtifactOptional =
                    implementationArtifactRepository.findByFileURL(implementationArtifact.getFileURL());
            if (persistedImplementationArtifactOptional.isPresent()) {
                implementationArtifact.setId(persistedImplementationArtifactOptional.get().getId());
            }
            Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
            implementationArtifact.setImplementation(implementation);
            return implementationArtifactRepository.save(implementationArtifact);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        } catch (StorageException e) {
            throw new CloudStorageException("could not create in storage");
        }
    }

    @Override
    public ImplementationArtifact findById(UUID id) {
        return ServiceUtils.findById(id, ImplementationArtifact.class, implementationArtifactRepository);
    }

    @Override
    public Page<ImplementationArtifact> findAllByImplementationId(UUID implementationId, Pageable pageable) {
        return implementationArtifactRepository.findImplementationArtifactsByImplementation_Id(implementationId, pageable);
    }

    @Override
    public byte[] getImplementationArtifactContent(UUID id) {
        ImplementationArtifact implementationArtifact = ServiceUtils.findById(id, ImplementationArtifact.class, implementationArtifactRepository);
        Blob blob = this.bucket.get(implementationArtifact.getFileURL());
        return blob.getContent();
    }

    @Override
    public ImplementationArtifact update(UUID id, MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        ImplementationArtifact storedEntity = this.findById(id);
        BlobId blobId = BlobId.of(implementationArtifactsBucketName, storedEntity.getFileURL());
        try {
            boolean wasDeleted = storage.delete(blobId);
            if (wasDeleted) {
                this.implementationArtifactRepository.delete(storedEntity);
            } else {
                throw new CloudStorageException("the blob was not found in the storage");
            }
        } catch (StorageException e) {
            throw new CloudStorageException("could not delete from storage");
        }

    }

    private ImplementationArtifact getImplementationArtifactFromBlob(Blob blob) {
        ImplementationArtifact implementationArtifact = new ImplementationArtifact();
        implementationArtifact.setName(blob.getName());
        implementationArtifact.setMimeType(blob.getContentType());
        implementationArtifact.setFileURL(blob.getName());
        implementationArtifact.setCreationDate(new Date(blob.getCreateTime()));
        implementationArtifact.setLastModifiedAt(new Date(blob.getUpdateTime()));
        return implementationArtifact;
    }
}
