package com.s3.operations.with.acl;

import java.io.IOException;
import java.util.ArrayList;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;

public class CreateS3BucketWithACL {
	 public static void main(String[] args) throws IOException {
		 
		 AWSCredentials credentials = new BasicAWSCredentials(
				  "AKIAQFG6IKOHVJZFYI56", 
				  "kWQl87pIRBqrSk9SZYr8D8wQ6WU43gn9ahxGr3v5"
				);
	        Regions clientRegion = Regions.DEFAULT_REGION;
	        String bucketName = "bucket1";
	        String userEmailForReadPermission = "vani.br50@gmail.com";

	        try {
	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	            		.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(clientRegion).build();
	           

	            // Create a bucket with a canned ACL. This ACL will be replaced by the setBucketAcl()
	            // calls below. It is included here for demonstration purposes.
	            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, clientRegion.getName())
	                    .withCannedAcl(CannedAccessControlList.LogDeliveryWrite);
	            s3Client.createBucket(createBucketRequest);

	            // Create a collection of grants to add to the bucket.
	            ArrayList<Grant> grantCollection = new ArrayList<Grant>();

	            // Grant the account owner full control.
	            CanonicalGrantee canIdentifier = new CanonicalGrantee(userEmailForReadPermission);
	            Grant grant1 = new Grant(canIdentifier, Permission.FullControl);
	            grantCollection.add(grant1);

	            // Grant the LogDelivery group permission to write to the bucket.
	            Grant grant2 = new Grant(GroupGrantee.LogDelivery, Permission.Write);
	            grantCollection.add(grant2);

	            // Save grants by replacing all current ACL grants with the two we just created.
	            AccessControlList bucketAcl = new AccessControlList();
	            bucketAcl.grantAllPermissions(grantCollection.toArray(new Grant[0]));
	            s3Client.setBucketAcl(bucketName, bucketAcl);

	            // Retrieve the bucket's ACL, add another grant, and then save the new ACL.
	            AccessControlList newBucketAcl = s3Client.getBucketAcl(bucketName);
	            Grant grant3 = new Grant(new EmailAddressGrantee(userEmailForReadPermission), Permission.Read);
	            newBucketAcl.grantAllPermissions(grant3);
	            s3Client.setBucketAcl(bucketName, newBucketAcl);
	        } catch (AmazonServiceException e) {
	            // The call was transmitted successfully, but Amazon S3 couldn't process 
	            // it and returned an error response.
	            e.printStackTrace();
	        } catch (SdkClientException e) {
	            // Amazon S3 couldn't be contacted for a response, or the client
	            // couldn't parse the response from Amazon S3.
	            e.printStackTrace();
	        }
	    }
}
