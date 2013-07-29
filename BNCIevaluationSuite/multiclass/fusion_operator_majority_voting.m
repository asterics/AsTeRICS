function maj_voted=fusion_operator_majority_voting(classes)

%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
%This function takes the matrix 'classes'
%and it outputs a column vector with the fusion decision, based on a
%democratic voting.
%If two classes are equally voted, the output for this case would be a -X
%where X is the decimal representation of a binary number, where a 1 appear
%in each class a draw occurs. As an example imagine we have
% 1 0 1 0, that means a draw occurs in class 1 and 3. The decimal output
% for this case would be -10.
%----------------------------------------------------------
%Inputs
% -classes: matrix M*N classifier outputs as input:
%
%       Classifier1 classifier2 ... classifierN
%test 1    1            2       ...     3
%test 2    1            2       ...     3
%...      ...      ...  ...     ...
%test M    1            2       ...     3
%----------------------------------------------------------
%Outputs
% -maj_voted:   a column vector with the fusion decision, based on a
%               democratic majority voting - Vector trials X 1
%----------------------------------------------------------
%Dependencies
%
%----------------------------------------------------------
% Version   Date        Author  Changes 
% v1        30/10/08    AR      First version
% v2        10/10/10    ASF     Header update.
%----------------------------------------------------------
% EX.
%
%--------------------------------------------------------------


number_of_classes=max(max(classes));

n = histc(classes,[1:number_of_classes],2);%I count how many votes each class has.

[a,maj_voted]=max(n,[],2);%I select the class with more votes.

%find the row indexes where a draw exist
index=[];
for i=1:length(a)
    if length(find(n(i,:)==a(i)))>1
        index=[index;i];
    end
end

%make all values diferent to 0 in 'n' equal to 1 (I pass 'n' to a binary format)
n(n~=0)=1;
to_binarize=n(index,:);

%binarize the rows where a draw occurs and put the binary code in his
%right place in the final output 'maj_voted'
all_str_bin=[];
for i=1:length(index)
str_bin=num2str(to_binarize(i,:));
a=bin2dec(str_bin);
all_str_bin=[all_str_bin,bin2dec(str_bin)];
maj_voted(index(i))=a*-1;
end
