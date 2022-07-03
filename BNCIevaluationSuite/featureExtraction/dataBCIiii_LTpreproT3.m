function class_epoch = dataBCIiii_LTpreproT3(class_epoch,dec)

%The function decimates and extracts the base line of each epoch
%This routine preprocess the data of the BCI competition to be used for
%Test3 of the PhD (RWD Ivan)
%data is explained at (http://ida.first.fraunhofer.de/projects/bci/competition_iii/desc_IIIa.pdf)
%processing steps:
% substract base line
% delmation by factor dec (int)
% class_epoch cell array of length numberOfEpochs in data set. The epochs
% are matrices of dimensions numberOfChannels X epochTimeSamples

%This function is obsolete (subsumed by starDecimate), but it is still
%used by Ivan.

for i=1:length(class_epoch)
    for j=1:length(class_epoch{i}(1,:))
        class_epoch_temp{i}(:,j)=decimate((class_epoch{i}(:,j)-mean(class_epoch{i}(:,j))),dec);
        %class_epoch_temp{i}(:,j)=class_epoch{i}(:,j)-mean(class_epoch{i}(501:750,j));
    end
end
clear class_epoch
class_epoch=class_epoch_temp;