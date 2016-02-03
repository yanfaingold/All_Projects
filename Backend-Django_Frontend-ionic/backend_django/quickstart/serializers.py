from django.contrib.auth.models import User
from rest_framework import serializers
from models import Question

class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ('url', 'username', 'email', 'groups')


class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Question
        fields = ('url','id', 'question_title',)
