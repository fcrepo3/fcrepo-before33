/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate;

import fedora.common.Constants;

import fedora.server.storage.types.RelationshipTuple;

/**
 * This is the actual validation engine, performing validation tests on a
 * {@link ValidationObject}. The engine is provided with an
 * {@link ObjectSource} at instantiation time, in case additional objects are
 * required to complete the validation.
 * 
 * @author Jim Blake
 */
public class ObjectValidator {

    private static final String DS_COMPOSITE_MODEL = "DS-COMPOSITE-MODEL";

    private final ObjectSource objectSource;

    public ObjectValidator(ObjectSource objectSource) {
        this.objectSource = objectSource;
    }

    /**
     * Each object is expected to have at least one content model. Validate each
     * of the content models.
     */
    public ValidationResult validate(ValidationObject object) {
        ValidationResult result = new ValidationResult(object);

        if (!object.hasRelation(Constants.MODEL.HAS_MODEL.uri)) {
            result.addNote(ValidationResultNotation.noContentModel());
            return result;
        }

        for (RelationshipTuple relation : object
                .getRelations(Constants.MODEL.HAS_MODEL.uri)) {
            validateContentModel(result, relation, object);
        }

        return result;
    }

    /**
     * <p>
     * Validate each content model relation.
     * </p>
     * <p>
     * If the content model relation doesn't point to an object PID, note that
     * and give up. Same if no object is found at that PID.
     * </p>
     * <p>
     * If we find an actual content model object, we expect it to have a
     * DS-COMPOSITE-MODEL datastream.
     * </p>
     */
    private void validateContentModel(ValidationResult result,
                                      RelationshipTuple relation,
                                      ValidationObject object) {
        String contentModelPid = relation.getObjectPID();

        if (contentModelPid == null) {
            result.addNote(ValidationResultNotation
                    .unrecognizedContentModelUri(relation.object));
            return;
        }

        ValidationObject contentModel;
        try {
            contentModel = objectSource.getValidationObject(contentModelPid);
            if (contentModel == null) {
                result.addNote(ValidationResultNotation
                        .contentModelNotFound(contentModelPid));
                return;
            }
        } catch (ObjectSourceException e) {
            result.addNote(ValidationResultNotation
                    .errorFetchingContentModel(contentModelPid, e));
            return;
        }

        if (!contentModel.getDatastreamIds().contains(DS_COMPOSITE_MODEL)) {
            result.addNote(ValidationResultNotation
                    .contentModelIsMissingDs(contentModel.getPid(),
                                             DS_COMPOSITE_MODEL));
        }
    }

}
