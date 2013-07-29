function [data,GT] = bci2Starlab(fileName)
load(fileName);
data=[];
GT.first = [];
GT.second = [];
GT.third = [];
matrix=['ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789_'];
for i=1:length(Signal(1,1,:)) % iterate through channels
    samples = [];
    for j=1:length(Signal(:,1,i)) % iterate through epochs
        gradient = [1 find(diff(StimulusCode(j,:))~=0)+1];
        gradient = [gradient length(StimulusCode(j,:))+1];
        for k=1:length(gradient)-1
            if Flashing(j,gradient(k))==1
                samples = [samples; Signal(j,gradient(k):gradient(k+1)-1,i)];
                if exist('StimulusType')
                    GT.first = [GT.first; StimulusType(j,gradient(k):gradient(k+1)-1)];
                end
                if exist('StimulusCode')
                    GT.second = [GT.second; StimulusCode(j,gradient(k):gradient(k+1)-1)];
                end
                if exist('TargetChar')
                    GT.third = [GT.third; find(matrix==TargetChar(j))];
                end
            end
        end
    end
    data(i,:,:)=samples';
end
