function confusionMat=classificationMat2confusionMat(classificationMat,subject)
%transforms a classification matrix into a confusion matrix for a
%particular subject
auxMatrix=classificationMat;
auxMatrix(subject,:)=[];
auxMatrix(:,subject)=[];
trueNegatives=sum(sum(auxMatrix));
truePositives=classificationMat(subject,subject);
falsePositives=sum(classificationMat(:,subject))-truePositives;
falseNegatives=sum(classificationMat(subject,:))-truePositives;
confusionMat=[truePositives,falseNegatives;falsePositives,trueNegatives];