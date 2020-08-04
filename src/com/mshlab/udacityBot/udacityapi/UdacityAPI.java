package com.mshlab.udacityBot.udacityapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mshlab.udacityBot.udacityapi.model.Response;
import com.mshlab.udacityBot.udacityapi.model.SubmissionRequest;
import com.mshlab.udacityBot.udacityapi.model.*;
import com.mshlab.udacityBot.udacityapi.model.User;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mshlab.udacityBot.udacityapi.MethodBuilder.TotalEarning;


public final class UdacityAPI {

    private static final String FORMAT = ".json";
    private static final String API_URL = "https://review-api.udacity.com/api/v1";
    private static final String LOGIN_URL = "https://user-api.udacity.com/signin";

    private static final String ME = "/me";
    private static final String CERTIFICATIONS = ME + "/certifications";
    private static final String SUBMISSIONS_ASSIGNED = ME + "/submissions/assigned";
    private static final String SUBMISSIONS_ASSIGNED_COUNT = ME + "/submissions/assigned_count";
    private static final String SUBMISSIONS = ME + "/submissions";
    private static final String STUDENT_FEEDBACKS = ME + "/student_feedbacks";
    private static final String STUDENT_FEEDBACKS_READ = "/student_feedbacks/%d/read";
    private static final String STUDENT_FEEDBACKS_STATS = ME + "/student_feedbacks/stats";
    private static final String GET_SUBMISSION_REQUESTS = ME + "/submission_requests";
    private static final String SUBMISSIONS_COMPLETED = ME + "/submissions/completed";
    private static final String PROJECT_ASSIGN_SUBMISSION = "/projects/%d/submissions/assign";
    private static final String PROJECT_INFO = "/projects/%d";
    private static final String SUBMISSION_INFO = "/submissions/%d";
    private static final String SUBMISSION_CONTENT = SUBMISSION_INFO + "/contents";
    private static final String SUBMISSION_AUDIT = SUBMISSION_INFO + "/audit";
    private static final String SUBMISSION_REQUESTS = "/submission_requests";
    private static final String ACTION_SUBMISSION_REQUESTS = SUBMISSION_REQUESTS + "/%d";
    private static final String SUBMISSION_REQUESTS_REFRESH = ACTION_SUBMISSION_REQUESTS + "/refresh";
    private static final String SUBMISSION_REQUESTS_WAIT = SUBMISSION_REQUESTS + "/%d" + "/waits";
    private static final String UNASSIGN_SUBMISSION_REQUESTS = SUBMISSION_REQUESTS + "/%d" + "/unassign";

    private static final String CONTENT_COMMENTS = "contents/%d" + "/comments";
    private static final String AUDIT_CRITIQUES = "/audits/%d/critiques";
    private static final Gson gsonObject;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        gsonObject = builder.create();
    }

    private String token;

    public UdacityAPI(String token) {
        this.token = token;
    }

    public UdacityAPI() {

    }

    private static String getMethod(String method) {
        return API_URL + method + FORMAT;
    }


    private <T> T getResponseObject(String method, REQUEST_TYPE requestType, String params, Class<T> aClass) throws UdacityException, IOException {
        return gsonObject.fromJson(getResponseObject(method, requestType, params), aClass);
    }

    private String getResponseObject(String method, REQUEST_TYPE requestType, String params) throws UdacityException, IOException {
        Response response = ApacheUtils.getResponse(getMethod(method), requestType, token, params);
        final String json = response.getContent();
        assertResponse(response, (UdacityException.UdacityExceptionHandler) statusCode -> {
            if (statusCode < 200 || statusCode >= 300) {
                throw new UdacityException(statusCode, gsonObject.fromJson(json, ErrorResponse.class));
            }
        });
        System.out.println(json);
        return json;
    }

    /**
     * Fetches info on the logged in reviewer
     *
     * @return User information
     * @throws IOException
     * @throws UdacityException
     */
    public User getMyInfo() throws IOException, UdacityException {
        return getResponseObject(ME, REQUEST_TYPE.GET, null, User.class);
    }

    /**
     * Returns all certifications for the logged in reviewer.
     *
     * @return Certification's array
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Certification[] getMyCertifications() throws IOException, UdacityException {
        return getResponseObject(CERTIFICATIONS, REQUEST_TYPE.GET, null, Certification[].class);
    }

    /**
     * Returns all submissions currently assigned to the logged in reviewer
     *
     * @return Assigned submission's array
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Submission[] getAssignedSubmissions() throws IOException, UdacityException {
        return getResponseObject(SUBMISSIONS_ASSIGNED, REQUEST_TYPE.GET, null, Submission[].class);
    }

    /**
     * Get submissions created by the authenticated user
     *
     * @return Submissions created by the authenticated user
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Submission[] getSubmissions() throws IOException, UdacityException {
        return getResponseObject(SUBMISSIONS, REQUEST_TYPE.GET, null, Submission[].class);
    }

    /**
     * Get submissions completed by the authenticated reviewer
     *
     * @return Submissions reviewed by the authenticated user
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Submission[] getSubmissionsCompleted() throws IOException, UdacityException {
        return getResponseObject(SUBMISSIONS_COMPLETED, REQUEST_TYPE.GET, null, Submission[].class);
    }

    /**
     * Get submissions completed by the authenticated reviewer
     *
     * @return Submissions reviewed by the authenticated user
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Submission[] getSubmissionsCompleted(Date startDate, Date endDate) throws IOException, UdacityException {
        String params = "start_date=" + String.valueOf(startDate.getTime()) + "&end_date=" + String.valueOf(endDate.getTime());
        return getResponseObject(SUBMISSIONS_COMPLETED, REQUEST_TYPE.GET, params, Submission[].class);
    }

    /**
     * Get the active submission request for the currently authenticated reviewer.
     *
     * @return The active ({@link SubmissionRequestStatus} == {@link SubmissionRequestStatus#AVAILABLE}) submission request objects. Under current constraints this should always be zero or one such requests.
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public SubmissionRequest[] getSubmissionsRequests() throws IOException, UdacityException {
        return getResponseObject(GET_SUBMISSION_REQUESTS, REQUEST_TYPE.GET, null, SubmissionRequest[].class);
    }


    /**
     * Get student feedback available for the authenticated reviewer.
     *
     * @param startDate Minimum  date
     * @param endDate   Maximum  date
     * @return Student feedback on submissions reviewed by authenticated user
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public StudentFeedback[] getStudentFeedbacks(Date startDate, Date endDate) throws IOException, UdacityException {
        String params = "start_date=" + String.valueOf(startDate.getTime()) + "&end_date=" + String.valueOf(endDate.getTime());
        return getResponseObject(STUDENT_FEEDBACKS, REQUEST_TYPE.GET, params, StudentFeedback[].class);
    }

    public void markStudentFeedbackAsRead(int feedbackId) throws UdacityException, IOException {
        getResponseObject(String.format(STUDENT_FEEDBACKS_READ, feedbackId), REQUEST_TYPE.PUT, null);
    }

    /**
     * Get student feedback available for the authenticated reviewer.
     *
     * @return Student feedback on submissions reviewed by authenticated user. It will supply feedback created within the last 30 days.
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public StudentFeedback[] getStudentFeedbacks() throws IOException, UdacityException {
        return getResponseObject(STUDENT_FEEDBACKS, REQUEST_TYPE.GET, null, StudentFeedback[].class);
    }

    /**
     * Returns count of all open submissions
     *
     * @return Number of submissions {@link SubmissionStatus#IN_REVIEW} assigned to user
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public int getSubmissionsAssignedCount() throws IOException, UdacityException {
        return getResponseObject(SUBMISSIONS_ASSIGNED_COUNT, REQUEST_TYPE.GET, null, CountResponse.class).getAssignedCount();
    }

    /**
     * Get information about student feedback available for the authenticated reviewer.
     *
     * @return Information about student feedback received by the authenticated user
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public StudentFeedbackStats getStudentFeedbackStats() throws IOException, UdacityException {
        return new StudentFeedbackStats(getResponseObject(STUDENT_FEEDBACKS_STATS, REQUEST_TYPE.GET, null, CountResponse.class));
    }

    /**
     * Attempt to assign an available submission of the specified project to the authenticated reviewer.
     *
     * @param projectID ID of the project to request assignment of a submission for.
     * @return Successfully assigned submission
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Submission assignSubmissionToProject(int projectID) throws IOException, UdacityException {
        return getResponseObject(String.format(PROJECT_ASSIGN_SUBMISSION, projectID), REQUEST_TYPE.POST, null, Submission.class);
    }

    /**
     * Create a new submission request for the authenticated reviewer
     *
     * @param submissionRequestProjects Project ID and language pairs you'd like to review.
     * @return Successfully created request
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public SubmissionRequest createSubmissionRequest(SubmissionRequestPair submissionRequestProjects) throws IOException, UdacityException {
        String json = gsonObject.toJson(submissionRequestProjects);
        return getResponseObject(SUBMISSION_REQUESTS, REQUEST_TYPE.POST, json, SubmissionRequest.class);
    }

    public SubmissionRequest createSubmissionRequestAllProjects() throws IOException, UdacityException {
        User user = getMyInfo();
        Certification[] myCertifications = getMyCertifications();

        SubmissionRequestPair submissionRequestProjects = new SubmissionRequestPair();
        List<SubmissionRequestProject> submissionRequestProjectList = new ArrayList<>();

        for (int i = 0; i < user.getMentorLanguages().size(); i++) {
            String language = user.getMentorLanguages().get(i);
            for (Certification certification : myCertifications) {
                SubmissionRequestProject submissionRequestProject = new SubmissionRequestProject();
                submissionRequestProject.setProjectId(certification.getProjectId());
                submissionRequestProject.setLanguage(language);
                submissionRequestProjectList.add(submissionRequestProject);
            }
        }
        submissionRequestProjects.setSubmissionRequestProjectList(submissionRequestProjectList);
        String json = gsonObject.toJson(submissionRequestProjects);
        return getResponseObject(SUBMISSION_REQUESTS, REQUEST_TYPE.POST, json, SubmissionRequest.class);
    }

    public Submission assignProjects() throws IOException, UdacityException {
        Certification[] myCertifications = getMyCertifications();
        Submission submission = null;

        for (Certification certification : myCertifications) {
            submission = assignSubmissionToProject(certification.getProjectId());
        }
        return submission;
    }


    /**
     * Terminate the submission request
     *
     * @param submissionId ID of the request owned by the currently authenticated reviewer
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public void deleteSubmissionRequest(int submissionId) throws IOException, UdacityException {
        getResponseObject(String.format(ACTION_SUBMISSION_REQUESTS, submissionId), REQUEST_TYPE.DELETE, null);
    }

    /**
     * Fetch a submission request by ID
     *
     * @param submissionId ID of the request owned by the currently authenticated reviewer
     * @return A submission request
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public SubmissionRequest getSubmissionRequest(int submissionId) throws IOException, UdacityException {
        return getResponseObject(String.format(ACTION_SUBMISSION_REQUESTS, submissionId), REQUEST_TYPE.GET, null, SubmissionRequest.class);
    }

    /**
     * Update the list of projects requested for an existing request
     *
     * @param submissionRequestId       ID of the request owned by the currently authenticated reviewer
     * @param submissionRequestProjects Project ID and language pairs you'd like to review.
     * @return A submission request
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public SubmissionRequest updateSubmissionRequest(int submissionRequestId, SubmissionRequestPair submissionRequestProjects) throws IOException, UdacityException {
        String json = gsonObject.toJson(submissionRequestProjects);
        return getResponseObject(String.format(ACTION_SUBMISSION_REQUESTS, submissionRequestId), REQUEST_TYPE.PUT, json, SubmissionRequest.class);
    }

    /**
     * Determine how many reviewers are ahead of the authenticated grader in queue
     *
     * @param submissionId ID of the request owned by the currently authenticated reviewer
     * @return Position in line awaiting reviews
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public SubmissionRequestWaitingList[] getSubmissionRequestWaitingList(int submissionId) throws IOException, UdacityException {
        return getResponseObject(String.format(SUBMISSION_REQUESTS_WAIT, submissionId), REQUEST_TYPE.GET, null, SubmissionRequestWaitingList[].class);
    }

    /**
     * Attempt to unassign the submission from the authenticated reviewer
     *
     * @param submissionId ID of the submission to unassign from the currently authenticated reviewer
     * @return Submission that was successfully unassigned
     * @throws IOException      if response is malformed
     * @throws UdacityException if status code is not 2xx
     */
    public Submission unassignSubmission(int submissionId) throws IOException, UdacityException {
        return getResponseObject(String.format(UNASSIGN_SUBMISSION_REQUESTS, submissionId), REQUEST_TYPE.PUT, null, Submission.class);
    }

    public String getTotalEarning(String date) throws IOException, UdacityException {
        return getResponseObject(TotalEarning(date), REQUEST_TYPE.GET, null, JsonObject.class).get("earnings").getAsString();
    }


    public SubmissionContent[] getSubmissionContents(int submissionId) throws IOException, UdacityException {
        return getResponseObject(String.format(SUBMISSION_CONTENT, submissionId), REQUEST_TYPE.GET, null, SubmissionContent[].class);
    }

    public AuditCritique[] getAuditCritiques(int auditId) throws IOException, UdacityException {
        return getResponseObject(String.format(AUDIT_CRITIQUES, auditId), REQUEST_TYPE.GET, null, AuditCritique[].class);
    }

    public SubmissionAudit getSubmissionAudit(int submissionId) throws IOException, UdacityException {
        return getResponseObject(String.format(SUBMISSION_AUDIT, submissionId), REQUEST_TYPE.GET, null, SubmissionAudit.class);
    }

    public ContentComment[] getContentComments(int contentId) throws IOException, UdacityException {
        return getResponseObject(String.format(CONTENT_COMMENTS, contentId), REQUEST_TYPE.GET, null, ContentComment[].class);
    }

    public SubmissionRequest refreshSubmissionRequest(int submissionId) throws IOException, UdacityException {
        return getResponseObject(String.format(SUBMISSION_REQUESTS_REFRESH, submissionId), REQUEST_TYPE.PUT, null, SubmissionRequest.class);
    }

    public ArrayList<SubmissionRequest> refreshAllSubmissionRequest() throws IOException, UdacityException {
        SubmissionRequest[] getSubmissionsRequest = getSubmissionsRequests();
        ArrayList<SubmissionRequest> refreshedSubmissionsRequest = new ArrayList<>();
        for (SubmissionRequest submissionRequest : getSubmissionsRequest) {
            SubmissionRequest request = getResponseObject(String.format(SUBMISSION_REQUESTS_REFRESH, submissionRequest.getId()), REQUEST_TYPE.PUT, null, SubmissionRequest.class);
            refreshedSubmissionsRequest.add(request);
        }
        return refreshedSubmissionsRequest;
    }


    public void deleteAllSubmissionRequest() throws IOException, UdacityException {
        SubmissionRequest[] getSubmissionsRequest = getSubmissionsRequests();
        ArrayList<SubmissionRequest> refreshedSubmissionsRequest = new ArrayList<>();
        for (SubmissionRequest submissionRequest : getSubmissionsRequest) {
            deleteSubmissionRequest(submissionRequest.getId());
        }
    }

    public Project getProjectInfo(int projectId) throws UdacityException, IOException {
        return getResponseObject(String.format(PROJECT_INFO, projectId), REQUEST_TYPE.GET, null, Project.class);
    }

    public Submission getSubmissionInfo(int submissionId) throws UdacityException, IOException {
        return getResponseObject(String.format(SUBMISSION_INFO, submissionId), REQUEST_TYPE.GET, null, Submission.class);
    }

    public <T> T personalizedRequest(String method, String params, REQUEST_TYPE requestType, Class<T> tClass) throws IOException {
        Response response = ApacheUtils.getResponse(getMethod(method), requestType, token, params);
        if (tClass == null) {
            return null;
        } else {
            return gsonObject.fromJson(response.getContent(), tClass);
        }
    }

    private static void assertResponse(Response response, UdacityException.UdacityExceptionHandler handler) throws UdacityException, IOException {
        handler.handleStatusCode(response.getStatusCode());
    }


    public String login(String email, String password) {
        String buildUrl = "https://user-api.udacity.com/signin";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        SignInObj signInObj = new SignInObj(email, password);
        String signJson = new Gson().toJson(signInObj, SignInObj.class);


        RequestBody requestBody = RequestBody.create(JSON, signJson);
        Request request = new Request.Builder()
                .url(buildUrl)
                .addHeader("Host", "user-api.udacity.com")
                .addHeader("Content-Length", "125")
                .addHeader("Accept", "application/json")
                .addHeader("Origin", "https://auth.udacity.com")
                .addHeader("Sec-Fetch-Site", "same-site")
                .addHeader("Referer", "https://auth.udacity.com/sign-in?next=https%3A%2F%2Fmentor-dashboard.udacity.com%2Freviews%2Foverview")
                .addHeader("Accept-Encoding", "application/json")
                .addHeader("Accept-Language", "en-US,en;q=0.9")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();
        try {
            com.squareup.okhttp.Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    String value = headers.value(i);
                    if (value.contains("_jwt")) {
                        return StringUtils.substringBetween(value, "_jwt=", ";");
                    }

                }
                return null;
            }


        } catch (IOException e) {
            return null;
        }

        return null;
    }

    public enum REQUEST_TYPE {
        GET,
        POST,
        DELETE,
        PUT
    }
}
